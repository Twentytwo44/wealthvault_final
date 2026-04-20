package com.wealthvault_final.`financial-asset`.ui.components.maptype

// 1. ประเภทบัญชีธนาคาร
val bankAccountTypes = listOf(
    "BANK_ACC_TYPE_SAVINGS" to "ออมทรัพย์",
    "BANK_ACC_TYPE_CURRENT" to "กระแสรายวัน",
    "BANK_ACC_TYPE_FIXED_DEPOSIT" to "ฝากประจำ"
)

// 2. ประเภทสิ่งปลูกสร้าง/ที่อยู่อาศัย
val buildingTypes = listOf(
    "BUILDING_TYPE_CONDO" to "คอนโด",
    "BUILDING_TYPE_HOUSE" to "บ้านเดี่ยว",
    "BUILDING_TYPE_TOWNHOME" to "ทาวน์โฮม",
    "BUILDING_TYPE_COMMERCIAL" to "อาคารพาณิชย์"
)

// 3. ประเภทประกัน
val insuranceTypes = listOf(
    "INSURANCE_TYPE_LIFE" to "ประกันชีวิต",
    "INSURANCE_TYPE_HEALTH" to "ประกันสุขภาพ",
    "INSURANCE_TYPE_ACCIDENT" to "ประกันอุบัติเหตุ",
    "INSURANCE_TYPE_PROPERTY" to "ประกันอัคคีภัย/ทรัพย์สิน",
    "INSURANCE_TYPE_VEHICLE" to "ประกันยานพาหนะ"
)

// 4. ประเภทการลงทุน
val investmentTypes = listOf(
    "INVEST_TYPE_STOCK_TH" to "หุ้นไทย",
    "INVEST_TYPE_STOCK_US" to "หุ้นต่างประเทศ (US)",
    "INVEST_TYPE_MUTUAL_FUND" to "กองทุนรวม",
    "INVEST_TYPE_BOND" to "พันธบัตร/หุ้นกู้",
    "INVEST_TYPE_CRYPTO" to "คริปโทเคอร์เรนซี",
    "INVEST_TYPE_GOLD" to "ทองคำ"
)

// 5. ประเภทหนี้สิน/ค่าใช้จ่าย
val liabilityTypes = listOf(
    "LIABILITY_TYPE_LOAN" to "สินเชื่อ / หนี้สิน",
)

val expenseTypes = listOf(

    "LIABILITY_TYPE_EXPENSE" to "ค่าใช้จ่าย"
)
