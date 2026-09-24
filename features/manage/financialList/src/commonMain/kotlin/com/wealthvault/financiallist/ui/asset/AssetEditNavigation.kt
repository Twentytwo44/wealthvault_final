package com.wealthvault.financiallist.ui.asset

import cafe.adriel.voyager.navigator.Navigator
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import com.wealthvault.domain.portfolio.BankAccountData
import com.wealthvault.domain.portfolio.BankAccountModel
import com.wealthvault.domain.portfolio.BuildingIdData
import com.wealthvault.domain.portfolio.BuildingModel
import com.wealthvault.domain.portfolio.CashIdData
import com.wealthvault.domain.portfolio.CashModel
import com.wealthvault.domain.portfolio.InsRefModel
import com.wealthvault.domain.portfolio.InsuranceIdData
import com.wealthvault.domain.portfolio.InsuranceModel
import com.wealthvault.domain.portfolio.InvestmentIdData
import com.wealthvault.domain.portfolio.LandIdData
import com.wealthvault.domain.portfolio.LandModel
import com.wealthvault.domain.portfolio.RefModel
import com.wealthvault.domain.portfolio.StockModel
import com.wealthvault.financiallist.ui.asset.form.account.BankAccountFormScreen
import com.wealthvault.financiallist.ui.asset.form.building.BuildingFormScreen
import com.wealthvault.financiallist.ui.asset.form.cash.CashFormScreen
import com.wealthvault.financiallist.ui.asset.form.insurance.InsuranceFormScreen
import com.wealthvault.financiallist.ui.asset.form.investment.StockFormScreen
import com.wealthvault.financiallist.ui.asset.form.land.LandFormScreen
import com.wealthvault.financiallist.ui.toAttachment

/** Maps domain detail values to the existing typed edit destinations. */
internal fun Navigator.openAssetEdit(rawData: Any) {
    when (rawData) {
        is BankAccountData -> {
            val dataToSend = BankAccountModel(
                type = rawData.type ?: "",
                name = rawData.name ?: "",
                bankName = rawData.bankName ?: "",
                bankId = rawData.bankAccount ?: "",
                amount = rawData.amount ?: Money(0),
                description = rawData.description ?: "",
                attachments = rawData.files?.map { it.toAttachment() } ?: emptyList(),
            )
            push(BankAccountFormScreen(rawData.id, dataToSend))
        }

        is CashIdData -> {
            val dataToSend = CashModel(
                cashName = rawData.name ?: "",
                amount = rawData.amount ?: Money(0),
                description = rawData.description ?: "",
                attachments = rawData.files?.map { it.toAttachment() } ?: emptyList(),
            )
            push(CashFormScreen(rawData.id, dataToSend))
        }

        is InvestmentIdData -> {
            val dataToSend = StockModel(
                stockName = rawData.name ?: "",
                quantity = FixedDecimal.fromDecimal(rawData.quantity?.decimalString(), scale = 4)
                    ?: FixedDecimal(0, 4),
                costPerPrice = rawData.costPerPrice ?: Money(0),
                description = rawData.description ?: "",
                attachments = rawData.files?.map { it.toAttachment() } ?: emptyList(),
                stockSymbol = "",
                brokerName = rawData.brokerName ?: "",
                type = rawData.type ?: "",
            )
            push(StockFormScreen(rawData.id, dataToSend))
        }

        is InsuranceIdData -> {
            val dataToSend = InsuranceModel(
                type = rawData.type ?: "",
                name = rawData.name ?: "",
                policyNumber = rawData.policyNumber ?: "",
                companyName = rawData.companyName ?: "",
                coveragePeriod = rawData.coveragePeriod.toString(),
                coverageAmount = rawData.coverageAmount ?: Money(0),
                conDate = rawData.conDate ?: "",
                expDate = rawData.expDate ?: "",
                description = rawData.description ?: "",
                attachments = rawData.files?.map { it.toAttachment() } ?: emptyList(),
            )
            push(InsuranceFormScreen(rawData.id, dataToSend))
        }

        is BuildingIdData -> {
            val dataToSend = BuildingModel(
                buildingName = rawData.name ?: "",
                area = rawData.area ?: 0.0,
                amount = rawData.amount ?: Money(0),
                description = rawData.description ?: "",
                attachments = rawData.files?.map { it.toAttachment() } ?: emptyList(),
                locationAddress = rawData.location?.address ?: "",
                locationSubDistrict = rawData.location?.subDistrict ?: "",
                locationDistrict = rawData.location?.district ?: "",
                locationProvince = rawData.location?.province ?: "",
                locationPostalCode = rawData.location?.postalCode ?: "",
                insIds = rawData.ins?.map { InsRefModel(insId = it.insId, insName = it.insName) } ?: emptyList(),
                referenceIds = rawData.referenceIds?.map { RefModel(areaName = it.refName, areaId = it.refId) } ?: emptyList(),
                type = rawData.type ?: "",
            )
            push(BuildingFormScreen(rawData.id ?: "", dataToSend))
        }

        is LandIdData -> {
            val dataToSend = LandModel(
                landName = rawData.name ?: "",
                area = rawData.area ?: 0.0,
                amount = rawData.amount ?: Money(0),
                description = rawData.description ?: "",
                attachments = rawData.files?.map { it.toAttachment() } ?: emptyList(),
                locationAddress = rawData.location?.address ?: "",
                locationSubDistrict = rawData.location?.subDistrict ?: "",
                locationDistrict = rawData.location?.district ?: "",
                locationProvince = rawData.location?.province ?: "",
                locationPostalCode = rawData.location?.postalCode ?: "",
                referenceIds = rawData.ref?.map { RefModel(areaName = it.refName, areaId = it.refId) } ?: emptyList(),
                deedNum = rawData.deedNum ?: "",
            )
            push(LandFormScreen(rawData.id, dataToSend))
        }
    }
}
