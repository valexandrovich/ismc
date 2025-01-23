<script setup>

import { Icon } from '@iconify/vue'
import TextInput from '@/components/controls/TextInput.vue'
import { computed, onMounted, reactive, watch } from 'vue'
import { cloneDeep } from 'lodash'
import { useUploadLeStore } from '@/stores/upload.le.js'


const props = defineProps({
  row: {
    required: true,
    type: Object,
  }
})


const localRow = reactive(cloneDeep(props.row));

const uploadLeStore = useUploadLeStore()


onMounted(() => {
  console.log('OM')
  saveChanges()
})


const saveChanges = () => {
  const updatedRow = uploadLeStore.updateRow(localRow.id, cloneDeep(localRow));
  if (updatedRow) {
    Object.assign(localRow, cloneDeep(updatedRow));
  } else {
    Object.assign(localRow, cloneDeep(props.row));
  }
};

const restoreChanges = () => {
  Object.assign(localRow, cloneDeep(props.row));
}

const isChanged = computed(() => {
  return Object.keys(props.row)
      .filter(key => key !== 'errors')
      .some(key => props.row[key] !== localRow[key]);
});

// Watch for changes in localRow to avoid direct mutations on props.row
watch(localRow, () => {
}, {deep: true});




</script>

<template>
  <div class="flex flex-col gap-6 mb-2 bg-white bg-opacity-70 dark:bg-opacity-10 p-6 rounded-lg"

  :class="Object.keys(localRow.errors).length > 0 ? 'outline-1 outline outline-offset-0 outline-red-400' : ''"
  >


    <div class="flex justify-between items-center pb-2">
      <span class="text-xs">ID: {{ localRow.id }}</span>
      <div class="flex items-center gap-2">
        <button @click="restoreChanges" :disabled="!isChanged" v-if="isChanged"
                class="flex items-center gap-2 text-teal-500 px-4 rounded-lg hover:text-teal-400">

          Відновити зміни
        </button>
        <button @click="saveChanges" :disabled="!isChanged" v-if="isChanged"
                class="flex items-center gap-2 bg-teal-600 px-4 rounded-lg hover:bg-teal-500 text-slate-50">
          <Icon icon="material-symbols:save" width="18"/>
          Зберігти
        </button>

      </div>
      <button class="text-red-400" @click="uploadLeStore.removeById(row.id)">
        <Icon icon="material-symbols:delete-outline" width="18px"/>
      </button>
    </div>


<!--    <span>{{uploadPpStore.tagTypes}}</span>-->


    <!--    IR: {{inputsRefs}}-->
    <!--    ALL VALID : {{allValid}}-->
    <!--    VS : {{validationStatuses}}-->

<!--    isChanged:: {{ isChanged }}-->
<!--    <div class="flex gap-6 text-xs">-->
<!--      <span>Store row</span>-->
<!--      <span>{{ row }}</span>-->
<!--      <span>Store VS</span>-->
<!--      <span>{{ row.errors }}</span>-->
<!--    </div>-->


<!--    <div class="flex gap-6 text-xs">-->
<!--      <span>Local row</span>-->
<!--      <span>{{ localRow }}</span>-->
<!--      <span>Local VS</span>-->
<!--      <span>{{ localRow.errors }}</span>-->
<!--    </div>-->



    <div class="flex gap-2">
      <TextInput label="Організаційно правова форма" placeholder="ПрАТ" v-model="localRow.opf"
                 :error-label="localRow.errors['patronymicNameUa']" class="flex-grow"/>
      <TextInput label="Коротка назва" placeholder="Рога та копита" v-model="localRow.shortName"
                 :error-label="localRow.errors['shortName']" class="flex-grow"/>



    </div>

    <div class="flex gap-2">


      <TextInput label="Повна назва" placeholder="Приватне Акціонерне Товариство Рога та копита" v-model="localRow.fullName"
                 :error-label="localRow.errors['fullName']" class="flex-grow"/>


    </div>





    <div class="flex gap-2 pl-6">

      <TextInput label="Дата заснування" placeholder="дд.мм.рррр" v-model="localRow.foundationDate"
                 :error-label="localRow.errors['foundationDate']" class="w-48"/>

      <TextInput label="Код ЄДРПОУ" placeholder="12345678" v-model="localRow.edrpou"
                 :error-label="localRow.errors['edrpou']" class="w-48"/>
    </div>





    <div class="flex gap-2">

      <TextInput label="Адреса (повністю)" placeholder="" v-model="localRow.addressSimple"
                 :error-label="localRow.errors['addressSimple']" class="flex-grow"/>
    </div>

    <div class="flex gap-2">

      <span>Адреса</span>
    </div>
    <div class="flex gap-2">

      <TextInput label="Поштовий код" placeholder="" v-model="localRow.addressZip"
                 :error-label="localRow.errors['addressSimple']" class="w-40"/>

      <TextInput label="Код країни" placeholder="" v-model="localRow.addressCountry"
                 :error-label="localRow.errors['addressSimple']" class="w-36"/>

      <TextInput label="Регіон (Область)" placeholder="" v-model="localRow.addressRegion"
                 :error-label="localRow.errors['addressSimple']" class="w-72"/>

      <TextInput label="Район" placeholder="" v-model="localRow.addressCounty"
                 :error-label="localRow.errors['addressSimple']" class="flex-grow"/>

    </div>
    <div class="flex gap-2">

      <TextInput label="Тип НП" placeholder="" v-model="localRow.addressCityType"
                 :error-label="localRow.errors['addressSimple']" class="w-52"/>

      <TextInput label="Назва НП" placeholder="" v-model="localRow.addressCityName"
                 :error-label="localRow.errors['addressSimple']" class="flex-grow"/>


      <TextInput label="Тип вулиці" placeholder="" v-model="localRow.addressStreetType"
                 :error-label="localRow.errors['addressSimple']" class="w-48"/>

      <TextInput label="Назва вулиці" placeholder="" v-model="localRow.addressStreetName"
                 :error-label="localRow.errors['addressSimple']" class="flex-grow"/>

    </div>
    <div class="flex gap-2">

      <TextInput label="Номер будівлі" placeholder="" v-model="localRow.addressBuildingNumber"
                 :error-label="localRow.errors['addressSimple']" class="flex-grow"/>

      <TextInput label="Номер будівлі (2)" placeholder="" v-model="localRow.addressBuildingPart"
                 :error-label="localRow.errors['addressSimple']" class="flex-grow"/>

      <TextInput label="Літера будівлі" placeholder="" v-model="localRow.addressBuildingLetter"
                 :error-label="localRow.errors['addressSimple']" class="flex-grow"/>

      <TextInput label="Номер квартири" placeholder="" v-model="localRow.addressApartment"
                 :error-label="localRow.errors['addressSimple']" class="flex-grow"/>
    </div>

    <div class="flex gap-2">

      <span>Мітка</span>
    </div>
    <div class="flex gap-2">

<!--      <select>-->
<!--        <option v-for="tag in uploadPpStore.tagTypes" :key="tag.code">{{tag.code}}</option>-->
<!--      </select>-->

<!--      <span>-->
<!--        {{}}-->
<!--      </span>-->

      <TextInput label="Код мітки" placeholder="" v-model="localRow.markId"
                 :error-label="localRow.errors['markId']" class="flex-grow"/>

      <TextInput label="Дата події" placeholder="" v-model="localRow.markEventDate"
                 :error-label="localRow.errors['markEventDate']" class="w-48"/>

      <TextInput label="Початок дії" placeholder="" v-model="localRow.markStartDate"
                 :error-label="localRow.errors['markStartDate']" class="w-48"/>

      <TextInput label="Кінець дії" placeholder="" v-model="localRow.markEndDate"
                 :error-label="localRow.errors['markEndDate']" class="w-48"/>
    </div>
    <div class="flex gap-2">
      <TextInput label="Текстове значення" placeholder="" v-model="localRow.markTextValue"
                 :error-label="localRow.errors['markTextValue']" class="flex-grow"/>

      <TextInput label="Числове значення" placeholder="" v-model="localRow.markNumberValue"
                 :error-label="localRow.errors['markNumberValue']" class="w-48"/>

      <TextInput label="Коментар" placeholder="" v-model="localRow.markComment"
                 :error-label="localRow.errors['markComment']" class="flex-grow"/>
    </div>

<!--    <div class="text-xs">-->
<!--      {{localRow}}-->
<!--    </div>-->

      <!--      <TextInput-->
      <!--          class="flex-grow"-->
      <!--          v-model="localRow.lNameUa"-->
      <!--          placeholder="Іванов"-->
      <!--          label="Прізвищє"-->
      <!--          @validation-status="handleValidationStatus( 'lastNameUa', $event)"-->
      <!--          :validationRules="[noSpaces, noDigits, noLatinLetters, noRuLetters, noSpecialCharsName]"-->
      <!--      />-->

      <!--      <TextInput-->
      <!--          class="flex-grow"-->
      <!--          v-model="localRow.fNameUa"-->
      <!--          placeholder="Іван"-->
      <!--          label="Ім'я"-->
      <!--          @validation-status="handleValidationStatus( 'firstNameUa', $event)"-->
      <!--          :validationRules="[noSpaces, noDigits, noLatinLetters, noRuLetters, noSpecialCharsName]"-->
      <!--      />-->

      <!--      <TextInput-->
      <!--          class="flex-grow"-->
      <!--          v-model="localRow.pNameUa"-->
      <!--          placeholder="Іванович"-->
      <!--          label="Пр-батькові"-->
      <!--          @validation-status="handleValidationStatus( 'patronymicNameUa', $event)"-->
      <!--          :validationRules="[noSpaces, noDigits, noLatinLetters, noRuLetters, noSpecialCharsName]"-->
      <!--      />-->

      <!--      <TextInput-->

      <!--          class="flex-grow"-->
      <!--          v-model="row.fNameUa"-->
      <!--          placeholder="Василь"-->
      <!--          label="Ім'я"-->
      <!--          @validation-status="handleValidationStatus('fNameUa',  $event)"-->
      <!--          :validationRules="[noSpaces, noDigits, noLatinLetters, noRuLetters, noSpecialCharsName]"-->
      <!--      />-->

      <!--      <TextInput-->

      <!--          class="flex-grow"-->
      <!--          v-model="row.pNameUa"-->
      <!--          placeholder="Петрович"-->

      <!--          label="По-батькові"-->
      <!--          :validationRules="[noSpaces, noDigits, noLatinLetters, noRuLetters, noSpecialCharsName]"-->
      <!--      />-->



    <!--    <div class="flex gap-2">-->
    <!--      <span><Icon icon="twemoji:flag-russia" class="mt-2"/></span>-->
    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.lNameRu"-->
    <!--          placeholder="Іванов"-->
    <!--          label="Прізвищє"-->
    <!--          :validationRules="[noSpaces, noDigits, noLatinLetters, noUaLetters, noSpecialCharsName]"-->
    <!--      />-->

    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.fNameRu"-->
    <!--          placeholder="Василь"-->
    <!--          label="Ім'я"-->
    <!--          :validationRules="[noSpaces, noDigits, noLatinLetters, noUaLetters, noSpecialCharsName]"-->
    <!--      />-->

    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.pNameRu"-->
    <!--          placeholder="Петрович"-->

    <!--          label="По-батькові"-->
    <!--          :validationRules="[noSpaces, noDigits, noLatinLetters, noUaLetters, noSpecialCharsName]"-->
    <!--      />-->
    <!--    </div>-->

    <!--    <div class="flex gap-2">-->
    <!--      <span><Icon icon="twemoji:flag-united-states" class="mt-2"/></span>-->
    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.lNameEn"-->
    <!--          placeholder="Іванов"-->
    <!--          label="Прізвищє"-->
    <!--          :validationRules="[noSpaces, noDigits, noCyrillicLetters, noSpecialCharsName]"-->
    <!--      />-->

    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.fNameEn"-->
    <!--          placeholder="Василь"-->
    <!--          label="Ім'я"-->
    <!--          :validationRules="[noSpaces, noDigits,  noCyrillicLetters, noSpecialCharsName]"-->
    <!--      />-->

    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.pNameEn"-->
    <!--          placeholder="Петрович"-->

    <!--          label="По-батькові"-->
    <!--          :validationRules="[noSpaces, noDigits, noCyrillicLetters, noSpecialCharsName]"-->
    <!--      />-->
    <!--    </div>-->

    <!--    <div class="flex gap-2">-->
    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.birthday"-->
    <!--          placeholder="дд.мм.рррр"-->
    <!--          label="Дата народження"-->
    <!--          :validationRules="[noSpaces, noLatinLetters, noCyrillicLetters, notDotsChars]"-->
    <!--      />-->

    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.inn"-->
    <!--          placeholder="XXXXXXXXXX"-->
    <!--          label="РНОКПП (ІПН)"-->
    <!--          :validationRules="[noSpaces, noLatinLetters, noCyrillicLetters, noSpecialChars]"-->
    <!--      />-->

    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.citizenship"-->
    <!--          placeholder="ISO код"-->
    <!--          label="Громадянство"-->
    <!--          :validationRules="[]"-->
    <!--      />-->

    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.sex"-->
    <!--          placeholder="M\F"-->
    <!--          label="Стать"-->
    <!--          :validationRules="[]"-->
    <!--      />-->

    <!--      &lt;!&ndash;      <SelectInput&ndash;&gt;-->
    <!--      &lt;!&ndash;          :is-allowed-null=false&ndash;&gt;-->
    <!--      &lt;!&ndash;                   :options="countryCodes"&ndash;&gt;-->
    <!--      &lt;!&ndash;          label="Громадянство"&ndash;&gt;-->
    <!--      &lt;!&ndash;          :model-value="row.citizenship"/>&ndash;&gt;-->

    <!--    </div>-->


    <!--    <div class="flex gap-2">-->

    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.birthplace"-->
    <!--          placeholder=""-->
    <!--          label="Місце народження"-->
    <!--          :validationRules="[]"-->
    <!--      />-->

    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.phone"-->
    <!--          placeholder=""-->
    <!--          label="Телефон"-->
    <!--          :validationRules="[]"-->
    <!--      />-->

    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.email"-->
    <!--          placeholder=""-->
    <!--          label="Електронна пошта"-->
    <!--          :validationRules="[]"-->
    <!--      />-->

    <!--    </div>-->


    <!--    <div class="flex flex-col gap-6">-->
    <!--      <div>-->
    <!--        <span class="text-sm">Паспорт громадянина України - книжка:</span>-->
    <!--      </div>-->
    <!--      <div class="flex gap-2">-->
    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.localPassSerial"-->
    <!--            placeholder=""-->
    <!--            label="Серія"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.localPassNum"-->
    <!--            placeholder=""-->
    <!--            label="Номер"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.localPassIssueDate"-->
    <!--            placeholder=""-->
    <!--            label="Дата видачі"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.localPassIssuer"-->
    <!--            placeholder=""-->
    <!--            label="Орган видачі"-->
    <!--            :validationRules="[]"-->
    <!--        />-->
    <!--      </div>-->

    <!--    </div>-->


    <!--    <div class="flex flex-col gap-4">-->
    <!--      <div>-->
    <!--        <span class="text-sm">Паспорт громадянина України - ID картка:</span>-->
    <!--      </div>-->
    <!--      <div class="flex gap-2">-->
    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.idPassNumber"-->
    <!--            placeholder=""-->
    <!--            label="Номер документу"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.idPassRecord"-->
    <!--            placeholder=""-->
    <!--            label="Номер запису в реєстрі"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.idPassIssueDate"-->
    <!--            placeholder=""-->
    <!--            label="Дата видачі"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.idPassIssuerCode"-->
    <!--            placeholder=""-->
    <!--            label="Код органа видачі"-->
    <!--            :validationRules="[]"-->
    <!--        />-->
    <!--      </div>-->

    <!--    </div>-->


    <!--    <div class="flex flex-col gap-4">-->
    <!--      <div>-->
    <!--        <span class="text-sm">Паспорт громадянина України для виїзду за кордон:</span>-->
    <!--      </div>-->
    <!--      <div class="flex gap-2">-->
    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.intPassSerial"-->
    <!--            placeholder=""-->
    <!--            label="Серія"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.intPassNumber"-->
    <!--            placeholder=""-->
    <!--            label="Номер"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.intPassIssueDate"-->
    <!--            placeholder=""-->
    <!--            label="Дата видачі"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.intPassIssuerCode"-->
    <!--            placeholder=""-->
    <!--            label="Код органа видачі"-->
    <!--            :validationRules="[]"-->
    <!--        />-->
    <!--      </div>-->
    <!--    </div>-->


    <!--    <div class="flex flex-col gap-4">-->
    <!--      <div>-->
    <!--        <span class="text-sm">Інший документ - посвідчення особи:</span>-->
    <!--      </div>-->
    <!--      <div class="flex gap-2">-->
    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.othPassName"-->
    <!--            placeholder=""-->
    <!--            label="Тип"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.intPassNumber"-->
    <!--            placeholder=""-->
    <!--            label="Номер"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.intPassIssueDate"-->
    <!--            placeholder=""-->
    <!--            label="Дата видачі"-->
    <!--            :validationRules="[]"-->
    <!--        />-->

    <!--        <TextInput-->
    <!--            ref="inputsRefs"-->
    <!--            class="flex-grow"-->
    <!--            v-model="row.intPassIssuerCode"-->
    <!--            placeholder=""-->
    <!--            label="Код органа видачі"-->
    <!--            :validationRules="[]"-->
    <!--        />-->
    <!--      </div>-->
    <!--    </div>-->

    <!--    <div class="flex flex-wrap  gap-4 text-xs">-->
    <!--      <TextInput-->
    <!--          ref="inputsRefs"-->
    <!--          class="flex-grow"-->
    <!--          v-model="row.addressBuildingPart"-->
    <!--          placeholder=""-->
    <!--          label="Адреса - номер будівлі (2)"-->
    <!--          :validationRules="[]"-->
    <!--      />-->
    <!--    </div>-->


    <!--    <div class="flex flex-wrap  gap-4 text-xs">-->
    <!--      <span>lNameUa: {{row.lNameUa}}</span>-->
    <!--      <span>fNameUa: {{row.fNameUa}}</span>-->
    <!--      <span>pNameUa: {{row.pNameUa}}</span>-->
    <!--      <span>lNameRu: {{row.lNameRu}}</span>-->
    <!--      <span>fNameRu: {{row.fNameRu}}</span>-->
    <!--      <span>pNameRu: {{row.pNameRu}}</span>-->
    <!--      <span>lNameEn: {{row.lNameEn}}</span>-->
    <!--      <span>fNameEn: {{row.fNameEn}}</span>-->
    <!--      <span>pNameEn: {{row.pNameEn}}</span>-->
    <!--      <span>birthday: {{row.birthday}}</span>-->
    <!--      <span>inn: {{row.inn}}</span>-->
    <!--      <span>localPassSerial: {{row.localPassSerial}}</span>-->
    <!--      <span>localPassNum: {{row.localPassNum}}</span>-->
    <!--      <span>localPassIssueDate: {{row.localPassIssueDate}}</span>-->
    <!--      <span>localPassIssuer: {{row.localPassIssuer}}</span>-->
    <!--      <span>idPassNumber: {{row.idPassNumber}}</span>-->
    <!--      <span>idPassRecord: {{row.idPassRecord}}</span>-->
    <!--      <span>idPassIssueDate: {{row.idPassIssueDate}}</span>-->
    <!--      <span>idPassIssuerCode: {{row.idPassIssuerCode}}</span>-->
    <!--      <span>intPassSerial: {{row.intPassSerial}}</span>-->
    <!--      <span>intPassNumber: {{row.intPassNumber}}</span>-->
    <!--      <span>intPassIssueDate: {{row.intPassIssueDate}}</span>-->
    <!--      <span>intPassIssuerCode: {{row.intPassIssuerCode}}</span>-->
    <!--      <span>othPassName: {{row.othPassName}}</span>-->
    <!--      <span>othPassNumber: {{row.othPassNumber}}</span>-->
    <!--      <span>othPassIssueDate: {{row.othPassIssueDate}}</span>-->
    <!--      <span>othPassExpiredDate: {{row.othPassExpiredDate}}</span>-->
    <!--      <span>othPassIssuerName: {{row.othPassIssuerName}}</span>-->
    <!--      <span>citizenship: {{row.citizenship}}</span>-->
    <!--      <span>birthplace: {{row.birthplace}}</span>-->
    <!--      <span>sex: {{row.sex}}</span>-->
    <!--      <span>phone: {{row.phone}}</span>-->
    <!--      <span>email: {{row.email}}</span>-->
    <!--      <span>addressSimple: {{row.addressSimple}}</span>-->
    <!--      <span>addressZip: {{row.addressZip}}</span>-->
    <!--      <span>addressCountry: {{row.addressCountry}}</span>-->
    <!--      <span>addressRegion: {{row.addressRegion}}</span>-->
    <!--      <span>addressCounty: {{row.addressCounty}}</span>-->
    <!--      <span>addressCityType: {{row.addressCityType}}</span>-->
    <!--      <span>addressCityName: {{row.addressCityName}}</span>-->
    <!--      <span>addressStreetType: {{row.addressStreetType}}</span>-->
    <!--      <span>addressStreetName: {{row.addressStreetName}}</span>-->
    <!--      <span>addressBuildingNo: {{row.addressBuildingNo}}</span>-->
    <!--      <span>addressBuildingPart: {{row.addressBuildingPart}}</span>-->
    <!--      <span>addressBuildingLetter: {{row.addressBuildingLetter}}</span>-->
    <!--      <span>addressApartment: {{row.addressApartment}}</span>-->
    <!--      <span>comment: {{row.comment}}</span>-->
    <!--      <span>markId: {{row.markId}}</span>-->
    <!--      <span>markEventDate: {{row.markEventDate}}</span>-->
    <!--      <span>markStartDate: {{row.markStartDate}}</span>-->
    <!--      <span>markEndDate: {{row.markEndDate}}</span>-->
    <!--      <span>markTextValue: {{row.markTextValue}}</span>-->
    <!--      <span>markNumberValue: {{row.markNumberValue}}</span>-->
    <!--      <span>markComment: {{row.markComment}}</span>-->
    <!--      <span>source: {{row.source}}</span>-->

    <!--    </div>-->


  </div>
</template>

<style scoped>

</style>