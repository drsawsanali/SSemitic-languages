package com.example

import com.example.data.repository.LexiconAndPhoneticsData
import com.example.ui.components.ArchaeologicalPeriod
import com.example.ui.components.ArchaeologicalSiteCategory
import com.example.ui.components.EpigraphicScriptFamily
import com.example.ui.components.LegendFilter
import com.example.ui.components.getSiteCategory
import com.example.ui.components.getSitePeriod
import com.example.ui.components.getSiteScriptFamily
import org.junit.Assert.*
import org.junit.Test

class MapLegendTest {

  @Test
  fun testAllSitesHaveValidPeriodClassification() {
    val sites = LexiconAndPhoneticsData.sites
    assertTrue("Sites should not be empty", sites.isNotEmpty())

    for (site in sites) {
      val period = getSitePeriod(site)
      assertNotNull("Period should not be null for site ${site.id}", period)
      assertTrue("Period title should not be blank", period.titleAr.isNotBlank())
      assertTrue("Time range should not be blank", period.timeRangeAr.isNotBlank())
      assertTrue("Marker hue should be between 0 and 360", period.markerHue in 0f..360f)
    }
  }

  @Test
  fun testAllSitesHaveValidCategoryClassification() {
    val sites = LexiconAndPhoneticsData.sites
    for (site in sites) {
      val category = getSiteCategory(site)
      assertNotNull("Category should not be null for site ${site.id}", category)
      assertTrue("Category icon should not be empty", category.icon.isNotBlank())
      assertTrue("Category title should not be blank", category.titleAr.isNotBlank())
      assertTrue("Category description should not be blank", category.descriptionAr.isNotBlank())
    }
  }

  @Test
  fun testAllSitesHaveValidScriptFamilyClassification() {
    val sites = LexiconAndPhoneticsData.sites
    for (site in sites) {
      val script = getSiteScriptFamily(site)
      assertNotNull("Script should not be null for site ${site.id}", script)
      assertTrue("Script glyph should not be blank", script.glyph.isNotBlank())
      assertTrue("Script title should not be blank", script.titleAr.isNotBlank())
      assertTrue("Script description should not be blank", script.descriptionAr.isNotBlank())
    }
  }

  @Test
  fun testAllPeriodsRepresented() {
    val sites = LexiconAndPhoneticsData.sites
    val sitePeriods = sites.map { getSitePeriod(it) }.toSet()

    assertTrue("Bronze age early should be represented", sitePeriods.contains(ArchaeologicalPeriod.EARLY_BRONZE))
    assertTrue("Bronze age late should be represented", sitePeriods.contains(ArchaeologicalPeriod.LATE_BRONZE))
    assertTrue("Iron age should be represented", sitePeriods.contains(ArchaeologicalPeriod.IRON_AGE))
    assertTrue("Classical Nabataean should be represented", sitePeriods.contains(ArchaeologicalPeriod.CLASSICAL_NABATAEAN))
    assertTrue("Late antiquity should be represented", sitePeriods.contains(ArchaeologicalPeriod.LATE_ANTIQUITY))
  }

  @Test
  fun testLegendFilterFormatting() {
    val periodFilter = LegendFilter.Period(ArchaeologicalPeriod.EARLY_BRONZE)
    assertEquals(ArchaeologicalPeriod.EARLY_BRONZE.titleAr, periodFilter.titleAr)
    assertEquals(ArchaeologicalPeriod.EARLY_BRONZE.color, periodFilter.color)

    val categoryFilter = LegendFilter.Category(ArchaeologicalSiteCategory.CAPITAL)
    assertTrue(categoryFilter.titleAr.contains("🏛️"))

    val scriptFilter = LegendFilter.Script(EpigraphicScriptFamily.CUNEIFORM)
    assertTrue(scriptFilter.titleAr.contains("楔"))
  }
}
