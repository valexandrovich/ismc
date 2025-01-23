export const spaceCheck =  '^[^\\s]+$';
export const digitCheck = '^[^\\d]+$';
export const ruLettersCheck = '^[^ыЫъЪёЁэЭ]+$';
export const uaLettersCheck = '^[^ЇїІіЄєҐґ]+$';
export const cyrillicLettersCheck = '^[^А-Яа-яЇїІіЄєҐґыЫъЪёЁэЭ]+$';
export const latinLettersCheck = '^[^A-Za-z]+$';
// export const latinLettersCheck = '^(?!$)[^A-Za-z]+$';

export const specialCharsNameCheck = '^[^)(="!@#$%^&*z`;_+~:.,?<>|/\\\\]+$';
export const specialCharsCheck = '^[^)(="!@#$%^&*z`;\\-\'_+~:.,?<>|/\\\\]+$';
// export const noDotsCharsCheck = '^[^)(="!@#$%^&*z`;_+~:-,?<>|/\\\\]+$';
export const noDotsCharsCheck = '^[^)(="!@#$%^&*z`\\-;_+~:,\'?<>|/\\\\]+$';