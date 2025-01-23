package ua.com.valexa.downloader.service.govua;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.valexa.afscommon.dto.cpms.StepRequestDto;
import ua.com.valexa.afscommon.dto.cpms.StepUpdateDto;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;
import ua.com.valexa.downloader.service.Downloadable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@Service("govua10")
public class Govua10 implements Downloadable {

    private static final Logger log = LoggerFactory.getLogger(Govua10.class);

    @Value("${downloader.mount.point}")
    private String mountPoint;

    @Value("${proxy.host}")
    private String proxyHost;

    @Value("${proxy.port}")
    private Integer proxyPort;

    final String getQueueCpmsStepUpdate;

    final ObjectMapper objectMapper;
    final RabbitTemplate rabbitTemplate;

    public Govua10(RabbitTemplate rabbitTemplate, String getQueueCpmsStepUpdate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.getQueueCpmsStepUpdate = getQueueCpmsStepUpdate;
    }

    @Override
    public StepUpdateDto handleStepRequest(StepRequestDto stepRequestDto) {

        StepUpdateDto stepUpdateDto = new StepUpdateDto();
        stepUpdateDto.setStepId(stepRequestDto.getStepId());
        try {
            stepUpdateDto.setStatus(TaskStatus.IN_PROGRESS);
            sendStepUpdate(stepUpdateDto);

            stepUpdateDto.setComment("Зчитування параметрів");
            sendStepUpdate(stepUpdateDto);
            StepParameters stepParameters = readParameters(stepRequestDto);

            stepUpdateDto.setComment("Зчитування метадати пакету");
            sendStepUpdate(stepUpdateDto);
            JsonNode packageMetadata = getPackageMetadata(stepParameters);

            stepUpdateDto.setComment("Зчитування актуального ресурсу");
            sendStepUpdate(stepUpdateDto);
            String actualResourceId = getActualResourceId(packageMetadata, stepParameters);

            stepUpdateDto.setComment("Зчитування метадати ресурсу");
            sendStepUpdate(stepUpdateDto);
            GovuaRevisionMetadata metadata = getRevisionMetadata(actualResourceId, stepParameters);

            String fileName = mountPoint + System.getProperty("file.separator") + stepRequestDto.getStepId() + "_" + stepRequestDto.getWorker() + "." + metadata.getFileExtension();

            stepUpdateDto.setComment("Завантаження файлу");
            sendStepUpdate(stepUpdateDto);
            String downloadedFile = downloadFile(metadata.getUrl(), fileName, stepParameters, stepUpdateDto);

            stepUpdateDto.setProgress(1.0);
            stepUpdateDto.setStatus(TaskStatus.FINISHED);
            stepUpdateDto.getResults().put("file", downloadedFile);


        } catch (Exception e) {
            log.error("Step ID: {}; Error while downloading: {}", stepRequestDto.getStepId(), e.getMessage());
            stepUpdateDto.setComment(e.getMessage());
            stepUpdateDto.setStatus(TaskStatus.FAILED);
            sendStepUpdate(stepUpdateDto);
        }
        return stepUpdateDto;
    }


    private JsonNode getPackageMetadata(StepParameters stepParameters) throws RuntimeException {
        log.info("Step ID: {}; Getting package metadata for: {}", stepParameters.getStepId() , stepParameters.getSourceName());
        try {
            int currentTry = 0;
            JsonNode jsonResponse = null;

            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(stepParameters.requestTimeoutSec * 1000)
                    .setConnectionRequestTimeout(stepParameters.requestTimeoutSec * 1000)
                    .setSocketTimeout(stepParameters.requestTimeoutSec * 1000)
                    .build();

            HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                    .setDefaultRequestConfig(config);

            if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != null) {
                HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                clientBuilder.setProxy(proxy);
            }

            CloseableHttpClient client = clientBuilder.build();

            while (currentTry < stepParameters.requestRetries) {
                try {
                    HttpGet request = new HttpGet("https://data.gov.ua/api/3/action/package_show?id=" + stepParameters.getPackageId());
                    request.addHeader("Content-Type", "application/json");
                    request.addHeader("Accept", "application/json");

                    HttpResponse response = client.execute(request);
                    String responseStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    jsonResponse = objectMapper.readTree(responseStr);
                    break;
                } catch (IOException e) {
                    currentTry++;
                    if (currentTry >= stepParameters.requestRetries) {
                        throw e;
                    }
                }
            }
            return jsonResponse;
        } catch (IOException e) {
            throw new RuntimeException("Can't get package metadata for " + stepParameters.getSourceName());
        }

    }

    private String getActualResourceId(JsonNode packageMetadata, StepParameters stepParameters) throws RuntimeException {
        log.info("Step ID: {}; Getting actual resource for: {}", stepParameters.getStepId(), stepParameters.getSourceName());
        try {
            JsonNode resources = packageMetadata.get("result").get("resources");
            return resources.get(resources.size() - 1).get("id").textValue();
        } catch (Exception e) {
            throw new RuntimeException("Can't get actual resource id for " + stepParameters.sourceName);
        }

    }

    private GovuaRevisionMetadata getRevisionMetadata(String resourceId, StepParameters stepParameters) throws RuntimeException {
        log.info("Step ID: {}; Getting revision metadata for: {}", stepParameters.getStepId(), stepParameters.sourceName);
        try {
            int currentTry = 0;
            JsonNode jsonResponse = null;

            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(stepParameters.requestTimeoutSec * 1000)
                    .setConnectionRequestTimeout(stepParameters.requestTimeoutSec * 1000)
                    .setSocketTimeout(stepParameters.requestTimeoutSec * 1000)
                    .build();

            HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                    .setDefaultRequestConfig(config);

            if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != null) {
                HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                clientBuilder.setProxy(proxy);
            }

            CloseableHttpClient client = clientBuilder.build();

            while (currentTry < stepParameters.requestRetries) {
                try {
                    HttpGet request = new HttpGet("https://data.gov.ua/api/3/action/resource_show?id=" + resourceId);
                    request.addHeader("Content-Type", "application/json");
                    request.addHeader("Accept", "application/json");

                    HttpResponse response = client.execute(request);
                    String responseStr = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    jsonResponse = objectMapper.readTree(responseStr);
                    String urlStr = jsonResponse.get("result").get("url").textValue();

                    GovuaRevisionMetadata metadata = new GovuaRevisionMetadata();
                    metadata.setUrl(new URL(urlStr));
                    metadata.setId(jsonResponse.get("result").get("revision_id").textValue());
                    metadata.setFileExtension(jsonResponse.get("result").get("format").textValue().toLowerCase());
                    return metadata;

                } catch (IOException e) {
                    currentTry++;
                    if (currentTry >= stepParameters.requestRetries) {
                        throw e;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Can't get actual file link for " + stepParameters.getSourceName());
        }

    }

    @SneakyThrows
    private String downloadFile(URL fileUrl, String filename, StepParameters stepParameters, StepUpdateDto stepUpdateDto) {
        log.info("Step ID: {}; Downloading file for: {}", stepParameters.getStepId(), stepParameters.sourceName);
        stepUpdateDto.setComment("Зчитування актуального ресурсу");
        sendStepUpdate(stepUpdateDto);

        File destination = new File(filename);

        long totalBytesRead = 0;
        int attempt = 0;
        long fileSize = getFileSize(fileUrl);

        try (RandomAccessFile destinationFile = new RandomAccessFile(destination, "rw")) {
            while (totalBytesRead < fileSize && attempt < stepParameters.getRequestRetries()) {
                try {
                    HttpURLConnection connection;

                    if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != null) {
                        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                        connection = (HttpURLConnection) fileUrl.openConnection(proxy);
                    } else {
                        connection = (HttpURLConnection) fileUrl.openConnection();
                    }

                    String byteRange = totalBytesRead + "-" + (fileSize - 1);
                    connection.setRequestProperty("Range", "bytes=" + byteRange);
                    connection.setReadTimeout(stepParameters.getRequestTimeoutSec() * 1000);

                    int lastPercentagePrinted = 0;
                    try (InputStream inputStream = connection.getInputStream()) {
                        byte[] buffer = new byte[1024 * 64];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            if (bytesRead == 0) {
                                break;
                            }
                            destinationFile.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;

                            int currentPercentage = (int) (100 * totalBytesRead / fileSize);
                            if (currentPercentage > lastPercentagePrinted) {
                                log.debug("Step ID: {}; Source: {};  Download progress: {}%", stepParameters.getStepId(), stepParameters.getSourceName(), currentPercentage);
                                String comment = "Завантажено " + totalBytesRead / 1000 / 1000 + " mb / " + fileSize / 1000 / 1000 + " mb";
                                stepUpdateDto.setComment(comment);
                                stepUpdateDto.setProgress((double) totalBytesRead / fileSize);
                                sendStepUpdate(stepUpdateDto);
                                lastPercentagePrinted = currentPercentage;
                            }
                        }
                    }
                    attempt = 0;
                } catch (IOException e) {
                    attempt++;
                    if (attempt >= stepParameters.getRequestRetries()) {
                        throw e;
                    }
                }
            }
        }
        stepUpdateDto.setComment("Завантажено " + totalBytesRead / 1000 / 1000 + " mb");
        sendStepUpdate(stepUpdateDto);
        return filename;
    }

    private long getFileSize(URL fileUrl) throws IOException {
        HttpURLConnection connection;

        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            connection = (HttpURLConnection) fileUrl.openConnection(proxy);
        } else {
            connection = (HttpURLConnection) fileUrl.openConnection();
        }

        connection.setRequestMethod("HEAD");
        long length = connection.getContentLengthLong();
        connection.disconnect();
        return length;
    }

    private StepParameters readParameters(StepRequestDto stepRequestDto) throws IllegalArgumentException {
        StepParameters stepParams = new StepParameters();

        stepParams.setStepId(stepRequestDto.getStepId());

        if (stepRequestDto.getParameters().containsKey("packageId")) {
            stepParams.setPackageId(stepRequestDto.getParameters().get("packageId"));
        } else {
            throw new IllegalArgumentException("Package ID is not present in StepRequest parameters!");
        }

        stepParams.setSourceName(stepRequestDto.getWorker());

        String retries = stepRequestDto.getParameters().getOrDefault("retries", "3");
        try {
            stepParams.setRequestRetries(Integer.parseInt(retries));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for 'retries'. It should be an integer. Now its value: " + retries);
        }

        String timeout = stepRequestDto.getParameters().getOrDefault("timeoutSec", "3");
        try {
            stepParams.setRequestTimeoutSec(Integer.parseInt(timeout));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format for 'timeoutSec'. It should be an integer. Now its value: " + timeout);
        }
        return stepParams;
    }

    private void sendStepUpdate(StepUpdateDto stepUpdateDto) {
        log.debug("Step ID: {}; Sending StepUpdate: {}", stepUpdateDto.getStepId(), stepUpdateDto);
        rabbitTemplate.convertAndSend(getQueueCpmsStepUpdate, stepUpdateDto);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private class StepParameters {
        private Long stepId;
        private String packageId;
        private String sourceName;
        private Integer requestRetries;
        private Integer requestTimeoutSec;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private class GovuaRevisionMetadata {
        private String id;
        private String fileExtension;
        private URL url;
    }

}
