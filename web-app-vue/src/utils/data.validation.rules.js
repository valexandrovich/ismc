

export const containsSpaces = {rule: /\s/, message: 'Не може містити пробіл'}
export const containsDigits = {rule: /\d/, message: 'Не може містити цифри'}


export const containsRuLetters =     { rule: /[ыЫъЪёЁэЭ]/, message: 'Не може містити російські літери' }
export const containsUaLetters =     { rule: /[ЇїІіЄєҐґ]/, message: 'Не може містити українські літери' }
export const containsCyrillicLetters =     { rule: /[А-Яа-яЇїІіЄєҐґыЫъЪёЁэЭ]/, message: 'Не може містити крилистичні літери' }
export const containsLatinLetters =     { rule: /[A-Za-z]/, message: 'Не може містити латинські літери' }
export const containsSpecialCharsName =     { rule: new RegExp('[)(="!@#$%^&*`;_+~:.,?<>|/\\\\]'), message: "Не може містити спецзнаки окрім - та '" }
// export const containsSpecialChars =     { rule: '^[^)(="!@#$%^&*z`;\\-\'_+~:.,?<>|/\\\\]+$', message: "Не може містити спецзнаки" }
// export const containsDotsChars =     { rule: '^[^)(="!@#$%^&*z`\\-;_+~:,\'?<>|/\\\\]+$', message: "Не може містити спецзнаки окрім ." }


export const localPassportSerialFormat = {rule: /^[\u0410-\u042F]{2}$/, message: 'Дві великі кирилистичні літери', mask: true}
export const localPassportNumberFormat = {rule: /^\d{6}$/, message: 'Необхідний формат XXXXXX', mask: true}


export const intPassportSerialFormat = {rule: /^[A-Z]{2}$/, message: 'Дві великі латинські літери', mask: true}
export const intPassportNumberFormat = {rule: /^\d{6}$/, message: 'Необхідний формат XXXXXX', mask: true}

export const idPassportNumberFormat = {rule: /^\d{9}$/, message: 'Необхідний формат XXXXXXXXX', mask: true}
export const idPassportRecordFormat = {rule: /^\d{8}-\d{5}$/, message: 'Необхідний формат XXXXXXXX-YYYYY', mask: true}
export const passportIssuerCodeFormat = {rule: /^\d{4,5}$/, message: '4 або 5 цифр', mask: true}

export const shortDateFormat = {rule: /^\d{2}\.\d{2}\.\d{4}$/, message: 'Необхідний формат дд.мм.рррр', mask: true};

export const innFormat = {rule: /^\d{9,10}$/, message: 'Має містити лише цифри. Довжина 9 або 10 символів', mask: true};