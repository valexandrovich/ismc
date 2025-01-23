import {
    cyrillicLettersCheck,
    digitCheck,
    latinLettersCheck, noDotsCharsCheck,
    ruLettersCheck,
    spaceCheck, specialCharsCheck, specialCharsNameCheck,
    uaLettersCheck
} from "@/utils/validationMasks.js";

export const noSpaces =     { mask: spaceCheck, message: 'Не може містити пробіл' }
export const noDigits =     { mask: digitCheck, message: 'Не може містити цифри' }
export const noRuLetters =     { mask: ruLettersCheck, message: 'Не може містити російські літери' }
export const noUaLetters =     { mask: uaLettersCheck, message: 'Не може містити українські літери' }
export const noCyrillicLetters =     { mask: cyrillicLettersCheck, message: 'Не може містити крилистичні літери' }
export const noLatinLetters =     { mask: latinLettersCheck, message: 'Не може містити латинські літери' }
export const noSpecialCharsName =     { mask: specialCharsNameCheck, message: "Не може містити спецзнаки окрім - та '" }
export const noSpecialChars =     { mask: specialCharsCheck, message: "Не може містити спецзнаки" }
export const notDotsChars =     { mask: noDotsCharsCheck, message: "Не може містити спецзнаки окрім ." }