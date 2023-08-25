package org.jellyfin.androidtv.util

import android.os.Build

object DeviceUtils {
	private const val CHROMECAST_GOOGLE_TV_PREFIX = "Chromecast"
	private const val FIRE_TV_PREFIX = "AFT"

	// https://storage.googleapis.com/play_public/supported_devices.html
	// Chromecast with Google TV Models
	private const val CHROMECAST_GOOGLE_TV_4K = "Chromecast"
	private const val CHROMECAST_GOOGLE_TV_HD = "Chromecast HD"

	// https://developer.amazon.com/docs/fire-tv/identify-amazon-fire-tv-devices.html
	// Fire TV Stick Models
	private const val FIRE_STICK_MODEL_GEN_1 = "AFTM"
	private const val FIRE_STICK_MODEL_GEN_2 = "AFTT"
	private const val FIRE_STICK_MODEL_GEN_3 = "AFTSSS"
	private const val FIRE_STICK_LITE_MODEL = "AFTSS"
	private const val FIRE_STICK_4K_MODEL = "AFTMM"
	private const val FIRE_STICK_4K_MAX_MODEL = "AFTKA"

	// Fire TV Cube Models
	private const val FIRE_CUBE_MODEL_GEN_1 = "AFTA"
	private const val FIRE_CUBE_MODEL_GEN_2 = "AFTR"

	// Fire TV (Box) Models
	private const val FIRE_TV_MODEL_GEN_1 = "AFTB"
	private const val FIRE_TV_MODEL_GEN_2 = "AFTS"
	private const val FIRE_TV_MODEL_GEN_3 = "AFTN"

	// Nvidia Shield TV Model
	private const val SHIELD_TV_MODEL = "SHIELD Android TV"

	private const val UNKNOWN = "Unknown"

	// Stub to allow for mock injection
	fun getBuildModel(): String = Build.MODEL ?: UNKNOWN

	@JvmStatic val isChromecastWithGoogleTv: Boolean get() = getBuildModel().startsWith(CHROMECAST_GOOGLE_TV_PREFIX)
	@JvmStatic val isChromecastWithGoogleTv4k: Boolean get() = getBuildModel() == CHROMECAST_GOOGLE_TV_4K
	@JvmStatic val isChromecastWithGoogleTvHd: Boolean get() = getBuildModel() == CHROMECAST_GOOGLE_TV_HD
	@JvmStatic val isFireTv: Boolean get() = getBuildModel().startsWith(FIRE_TV_PREFIX)
	@JvmStatic val isFireTvStickGen1: Boolean get() = getBuildModel() == FIRE_STICK_MODEL_GEN_1
	@JvmStatic val isFireTvStick4k: Boolean get() = getBuildModel() in listOf(FIRE_STICK_4K_MODEL, FIRE_STICK_4K_MAX_MODEL)
	@JvmStatic val isShieldTv: Boolean get() = getBuildModel() == SHIELD_TV_MODEL

	@JvmStatic
	fun has4kVideoSupport(): Boolean = getBuildModel() != UNKNOWN && getBuildModel() !in listOf(
		// These devices only support a max video resolution of 1080p
		CHROMECAST_GOOGLE_TV_HD,
		FIRE_STICK_MODEL_GEN_1,
		FIRE_STICK_MODEL_GEN_2,
		FIRE_STICK_MODEL_GEN_3,
		FIRE_STICK_LITE_MODEL,
		FIRE_TV_MODEL_GEN_1,
		FIRE_TV_MODEL_GEN_2
	)

	@JvmStatic
	fun is60(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}
