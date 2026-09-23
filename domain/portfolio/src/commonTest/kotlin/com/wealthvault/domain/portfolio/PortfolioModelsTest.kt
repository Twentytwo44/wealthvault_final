package com.wealthvault.domain.portfolio

import com.wealthvault.core.model.Attachment
import com.wealthvault.core.model.AttachmentType
import com.wealthvault.core.model.FixedDecimal
import com.wealthvault.core.model.Money
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PortfolioModelsTest {
    @Test
    fun portfolioReadModelsUseDomainMoneyAndSafeDefaults() {
        val money = Money(10_000)
        val quantity = FixedDecimal(25, 2)
        val file = AssetFile("file-1", "https://example.test/file", "image")
        val location = BuildingLocation(address = "1 Main Street", province = "Bangkok")
        val buildingInsurance = BuildingInsurance("ins-1", "Home")
        val buildingReference = BuildingReference("ref-1", "Residential")
        val landLocation = LandLocation(address = "2 Main Street", province = "Bangkok")
        val landReference = LandReference("ref-2", "Land")

        val account = BankAccountData("account-1", "user-1", amount = money, files = listOf(file))
        val accountList = AccountData("account-1", "user-1", amount = money)
        val building = BuildingIdData(
            id = "building-1",
            userId = "user-1",
            amount = money,
            location = location,
            ins = listOf(buildingInsurance),
            referenceIds = listOf(buildingReference),
            files = listOf(file),
        )
        val buildingList = GetBuildingData(location = location, amount = money)
        val cash = CashIdData("cash-1", "user-1", amount = money, files = listOf(file))
        val cashList = CashData(id = "cash-1", amount = money)
        val legacyCash = GetCashData(id = "cash-1", ammount = money)
        val insurance = InsuranceIdData("insurance-1", "user-1", coverageAmount = money, files = listOf(file))
        val insuranceList = GetInsuranceData(id = "insurance-1", coverageAmount = money)
        val investment = InvestmentIdData("investment-1", "user-1", quantity = quantity, costPerPrice = money, amount = money, files = listOf(file))
        val investmentList = GetInvestmentData(id = "investment-1", quantity = quantity, costPerPrice = money, amount = money)
        val land = LandIdData("land-1", "user-1", amount = money, location = landLocation, files = listOf(file), ref = listOf(landReference))
        val landList = GetLandData(id = "land-1", amount = money, location = landLocation)
        val liability = LiabilityIdData("liability-1", "user-1", principal = money, interestRate = quantity, files = listOf(file))
        val liabilityList = GetLiabilityData(id = "liability-1", principal = money, interestRate = quantity)

        assertEquals(money, account.amount)
        assertEquals(money, accountList.amount)
        assertEquals(buildingInsurance, building.ins?.single())
        assertEquals(location, buildingList.location)
        assertEquals(money, cash.amount)
        assertEquals(money, legacyCash.ammount)
        assertEquals(money, insuranceList.coverageAmount)
        assertEquals(quantity, investment.quantity)
        assertEquals(quantity, investmentList.quantity)
        assertEquals(landReference, land.ref?.single())
        assertEquals(money, landList.amount)
        assertEquals(quantity, liability.interestRate)
        assertEquals(money, liabilityList.principal)
        assertEquals(AssetFile(), AssetFile())
        assertEquals(BuildingIdData(), BuildingIdData())
        assertEquals(GetInvestmentData(), GetInvestmentData())
    }

    @Test
    fun portfolioCommandsAndFormModelsStayTransportNeutral() {
        val money = Money(10_000)
        val quantity = FixedDecimal(25, 2)
        val attachment = Attachment("receipt", AttachmentType.IMAGE)
        val bytes = byteArrayOf(1, 2, 3)

        assertEquals(BuildingData(id = "building-1", amount = money), BuildingData(id = "building-1", amount = money))
        assertEquals(InsuranceData(id = "insurance-1", coverageAmount = money), InsuranceData(id = "insurance-1", coverageAmount = money))
        assertEquals(InvestmentData(id = "investment-1", quantity = quantity), InvestmentData(id = "investment-1", quantity = quantity))
        assertEquals(LandData(id = "land-1", amount = money), LandData(id = "land-1", amount = money))
        assertEquals(LiabilityData(id = "liability-1", principal = money, interestRate = quantity), LiabilityData(id = "liability-1", principal = money, interestRate = quantity))

        assertEquals(BuildingReferenceData("Area", "area-1"), BuildingReferenceData("Area", "area-1"))
        assertEquals(InsReferenceData("Home", "ins-1"), InsReferenceData("Home", "ins-1"))
        assertEquals(LandReferenceData("Land", "land-1"), LandReferenceData("Land", "land-1"))
        assertEquals("image/png", BankAccountFileUploadData(bytes, "image/png", "receipt.png").mimeType)
        assertEquals("receipt.png", BuildingFileUploadData(bytes, "image/png", "receipt.png").fileName)
        assertEquals(bytes.size, CashFileUploadData(bytes).bytes?.size)
        assertEquals(bytes.size, InsuranceFileUploadData(bytes).bytes?.size)
        assertEquals(bytes.size, FileUploadData(bytes).bytes?.size)
        assertEquals(bytes.size, LandFileUploadData(bytes).bytes?.size)
        assertEquals("receipt.png", LiabilityUploadData(bytes, "image/png", "receipt.png").fileName)

        val accountRequest = BankAccountRequest("Savings", "Bank", "123", "cash", money, "description")
        assertEquals(money, accountRequest.amount)
        assertTrue(BuildingRequest().insIds.isEmpty())
        assertEquals(CashRequest(amount = money), CashRequest(amount = money))
        assertEquals(InsuranceRequest(coverageAmount = money), InsuranceRequest(coverageAmount = money))
        assertEquals(
            InvestmentRequest(
                quantity = FixedDecimal.fromDecimal("2.5", scale = 4),
                costPerPrice = Money.fromDecimal("100"),
            ),
            InvestmentRequest(
                quantity = FixedDecimal.fromDecimal("2.5", scale = 4),
                costPerPrice = Money.fromDecimal("100"),
            ),
        )
        assertEquals(LandRequest(amount = money), LandRequest(amount = money))
        assertEquals(LiabilityRequest(principal = money), LiabilityRequest(principal = money))

        val bankModel = BankAccountModel("cash", "Savings", money, "Bank", "bank-1", "description", listOf(attachment))
        val buildingModel = BuildingModel("home", "House", 120.0, money, "description", listOf(attachment), listOf(RefModel("Area", "area-1")), "address", "sub", "district", "province", "10100", listOf(InsRefModel("Home", "ins-1")))
        val cashModel = CashModel("Cash", money, "description", listOf(attachment))
        val insuranceModel = InsuranceModel("P-1", "life", "Insurer", money, "12", "2026-12-31", "description", listOf(attachment), "2026-01-01", "Policy")
        val stockModel = StockModel("Stock", quantity, "description", "ABC", "Broker", money, listOf(attachment), "equity")
        val landModel = LandModel("D-1", "Land", 80.0, money, "description", listOf(attachment), listOf(RefModel("Area", "area-1")), "address", "sub", "district", "province", "10100")

        assertEquals("Savings", bankModel.name)
        assertEquals("House", buildingModel.buildingName)
        assertEquals("Cash", cashModel.cashName)
        assertEquals("Policy", insuranceModel.name)
        assertEquals(quantity, stockModel.quantity)
        assertEquals("Land", landModel.landName)
        assertEquals(RefModel("Area", "area-1"), RefModel("Area", "area-1"))
        assertEquals(InsRefModel("Home", "ins-1"), InsRefModel("Home", "ins-1"))
    }

    @Test
    fun obligationModelsShareTheSameFixedPointMoneyBoundary() {
        val money = Money(20_000)
        val attachment = Attachment("invoice", AttachmentType.PDF)
        val expense = ExpenseModel("expense", "Rent", money, "3.5", "monthly", "2026-01-01", "2026-12-31", "Landlord", listOf(attachment))
        val liability = LiabilityModel("loan", "Mortgage", money, "3.5", "home loan", "2026-01-01", "2036-12-31", "Bank", listOf(attachment))

        assertEquals(money, expense.principal)
        assertEquals(money, liability.principal)
        assertEquals(AttachmentType.PDF, liability.attachments.single().type)
    }
}
