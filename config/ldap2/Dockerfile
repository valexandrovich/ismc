FROM osixia/openldap:latest

COPY example.ldif /container/service/slapd/assets/config/bootstrap/ldif/50-example.ldif
RUN chown -R openldap:openldap /container/service/slapd/assets/config/bootstrap/ldif

ENV LDAP_ORGANISATION="Example-com LLC"
ENV LDAP_DOMAIN="example.com"
ENV LDAP_ADMIN_PASSWORD="3211"
ENV CONTAINER_LOG_LEVEL=debug

EXPOSE 389 636