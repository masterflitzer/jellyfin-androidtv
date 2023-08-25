package org.jellyfin.androidtv.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject

class DeviceUtilsTests : FunSpec({
	test("DeviceUtils.getBuildModel() is unknown") {
		DeviceUtils.getBuildModel() shouldBe "Unknown"
	}

	test("DeviceUtils methods support unknown as model") {
		DeviceUtils.isChromecastWithGoogleTv shouldBe false
		DeviceUtils.isChromecastWithGoogleTv4k shouldBe false
		DeviceUtils.isChromecastWithGoogleTvHd shouldBe false
		DeviceUtils.isFireTv shouldBe false
		DeviceUtils.isFireTvStickGen1 shouldBe false
		DeviceUtils.isFireTvStick4k shouldBe false
		DeviceUtils.isShieldTv shouldBe false
		DeviceUtils.has4kVideoSupport() shouldBe false
	}

	fun withBuildModel(buildModel: String, block: () -> Unit) {
		mockkObject(DeviceUtils)
		every { DeviceUtils.getBuildModel() } returns buildModel
		block()
		unmockkObject(DeviceUtils)
	}

	test("DeviceUtils.isChromecastWithGoogleTv() works correctly") {
		arrayOf("Chromecast", "Chromecast_foo", "Chromecast ", "Chromecast2").forEach { input ->
			withBuildModel(input) {
				DeviceUtils.isChromecastWithGoogleTv shouldBe true
			}
		}
	}

	test("DeviceUtils.isChromecastWithGoogleTv4k() works correctly") {
		withBuildModel("Chromecast") {
			DeviceUtils.isChromecastWithGoogleTv4k shouldBe true
			DeviceUtils.isChromecastWithGoogleTvHd shouldBe false
		}
	}

	test("DeviceUtils.isChromecastWithGoogleTvHd() works correctly") {
		withBuildModel("Chromecast HD") {
			DeviceUtils.isChromecastWithGoogleTv4k shouldBe false
			DeviceUtils.isChromecastWithGoogleTvHd shouldBe true
		}
	}

	test("DeviceUtils.isFireTv() works correctly") {
		arrayOf("AFT", "AFT_foo", "AFT ", "AFT2").forEach { input ->
			withBuildModel(input) {
				DeviceUtils.isFireTv shouldBe true
			}
		}
	}

	test("DeviceUtils.isFireTvStickGen1() works correctly") {
		withBuildModel("AFTM") {
			DeviceUtils.isFireTv shouldBe true
			DeviceUtils.isFireTvStickGen1 shouldBe true
			DeviceUtils.isFireTvStick4k shouldBe false
		}
	}

	test("DeviceUtils.isFireTvStick4k() works correctly") {
		arrayOf("AFTMM", "AFTKA").forEach { input ->
			withBuildModel(input) {
				DeviceUtils.isFireTv shouldBe true
				DeviceUtils.isFireTvStick4k shouldBe true
				DeviceUtils.isFireTvStickGen1 shouldBe false
			}
		}
	}

	test("DeviceUtils.isShieldTv() works correctly") {
		withBuildModel("SHIELD Android TV") {
			DeviceUtils.isShieldTv shouldBe true
		}
	}

	test("DeviceUtils.has4kVideoSupport() works correctly") {
		arrayOf("Chromecast HD", "AFTM", "AFTT", "AFTSSS", "AFTSS", "AFTB", "AFTS").forEach { input ->
			withBuildModel(input) {
				DeviceUtils.has4kVideoSupport() shouldBe false
			}
		}
		arrayOf("Chromecast", "AFTMM", "AFTKA").forEach { input ->
			withBuildModel(input) {
				DeviceUtils.has4kVideoSupport() shouldBe true
			}
		}
	}
})
