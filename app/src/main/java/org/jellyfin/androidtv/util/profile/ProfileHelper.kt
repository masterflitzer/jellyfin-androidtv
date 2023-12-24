package org.jellyfin.androidtv.util.profile

import org.jellyfin.androidtv.constant.Codec
import org.jellyfin.androidtv.util.DeviceUtils
import org.jellyfin.apiclient.model.dlna.CodecProfile
import org.jellyfin.apiclient.model.dlna.CodecType
import org.jellyfin.apiclient.model.dlna.DirectPlayProfile
import org.jellyfin.apiclient.model.dlna.DlnaProfileType
import org.jellyfin.apiclient.model.dlna.ProfileCondition
import org.jellyfin.apiclient.model.dlna.ProfileConditionType
import org.jellyfin.apiclient.model.dlna.ProfileConditionValue
import org.jellyfin.apiclient.model.dlna.SubtitleDeliveryMethod
import org.jellyfin.apiclient.model.dlna.SubtitleProfile
import timber.log.Timber

object ProfileHelper {
	// H264 codec levels https://en.wikipedia.org/wiki/Advanced_Video_Coding#Levels
	private const val H264_LEVEL_4_1 = "41"
	private const val H264_LEVEL_4_2 = "42"
	private const val H264_LEVEL_5_1 = "51"
	private const val H264_LEVEL_5_2 = "52"

	private val MediaTest by lazy { MediaCodecCapabilitiesTest() }

	val deviceAv1CodecProfile by lazy {
		CodecProfile().apply {
			type = CodecType.Video
			codec = Codec.Video.AV1

			conditions = when {
				!MediaTest.supportsAv1() -> {
					// The following condition is a method to exclude all AV1
					Timber.i("*** Does NOT support AV1")
					arrayOf(
						ProfileCondition(
							ProfileConditionType.Equals,
							ProfileConditionValue.VideoProfile,
							"none"
						)
					)
				}
				!MediaTest.supportsAv1Main10() -> {
					Timber.i("*** Does NOT support AV1 10 bit")
					arrayOf(
						ProfileCondition(
							ProfileConditionType.NotEquals,
							ProfileConditionValue.VideoProfile,
							"Main 10"
						)
					)
				}
				else -> {
					// supports all AV1
					Timber.i("*** Supports AV1 10 bit")
					arrayOf(
						ProfileCondition(
							ProfileConditionType.NotEquals,
							ProfileConditionValue.VideoProfile,
							"none"
						)
					)
				}
			}
		}
	}

	val deviceHevcCodecProfile by lazy {
		CodecProfile().apply {
			type = CodecType.Video
			codec = Codec.Video.HEVC

			conditions = when {
				!MediaTest.supportsHevc() -> {
					// The following condition is a method to exclude all HEVC
					Timber.i("*** Does NOT support HEVC")
					arrayOf(
						ProfileCondition(
							ProfileConditionType.Equals,
							ProfileConditionValue.VideoProfile,
							"none"
						)
					)
				}
				!MediaTest.supportsHevcMain10() -> {
					Timber.i("*** Does NOT support HEVC 10 bit")
					arrayOf(
						ProfileCondition(
							ProfileConditionType.NotEquals,
							ProfileConditionValue.VideoProfile,
							"Main 10"
						)
					)
				}
				else -> {
					// supports all HEVC
					Timber.i("*** Supports HEVC 10 bit")
					arrayOf(
						ProfileCondition(
							ProfileConditionType.NotEquals,
							ProfileConditionValue.VideoProfile,
							"none"
						)
					)
				}
			}
		}
	}

	val h264VideoLevelProfileCondition by lazy {
		ProfileCondition(
			ProfileConditionType.LessThanEqual,
			ProfileConditionValue.VideoLevel,
			when {
				// https://support.google.com/chromecast/answer/3046409
				DeviceUtils.isChromecastWithGoogleTv4k -> H264_LEVEL_5_2
				DeviceUtils.isChromecastWithGoogleTvHd -> H264_LEVEL_4_2
				// https://developer.amazon.com/docs/fire-tv/device-specifications.html
				DeviceUtils.isFireTvStick4k -> H264_LEVEL_5_2
				DeviceUtils.isFireTv -> H264_LEVEL_4_1
				DeviceUtils.isShieldTv -> H264_LEVEL_5_2
				else -> H264_LEVEL_5_1
			}
		)
	}

	val h264VideoProfileCondition by lazy {
		ProfileCondition(
			ProfileConditionType.EqualsAny,
			ProfileConditionValue.VideoProfile,
			listOf(
				if (MediaTest.supportsAvcHigh10()) "high 10" else null,
				"high",
				"main",
				"baseline",
				"constrained baseline",
			).joinToString("|")
		)
	}

	val max1080pProfileConditions by lazy {
		arrayOf(
			ProfileCondition(
				ProfileConditionType.LessThanEqual,
				ProfileConditionValue.Width,
				"1920"
			),
			ProfileCondition(
				ProfileConditionType.LessThanEqual,
				ProfileConditionValue.Height,
				"1080"
			)
		)
	}

	val photoDirectPlayProfile by lazy {
		DirectPlayProfile().apply {
			type = DlnaProfileType.Photo
			container = listOfNotNull(
				"png",
				"jpg",
				"jpeg",
				"gif",
				"webp",
				if (MediaTest.supportsHevc()) "heic" else null,
				if (MediaTest.supportsHevc()) "heif" else null,
				if (MediaTest.supportsAv1()) "avif" else null
			).joinToString(",")
		}
	}

	fun audioDirectPlayProfile(containers: Array<String>) = DirectPlayProfile()
		.apply {
			type = DlnaProfileType.Audio
			container = containers.joinToString(",")
		}

	fun maxAudioChannelsCodecProfile(channels: Int) = CodecProfile()
		.apply {
			type = CodecType.VideoAudio
			conditions = arrayOf(
				ProfileCondition(
					ProfileConditionType.LessThanEqual,
					ProfileConditionValue.AudioChannels,
					channels.toString()
				)
			)
		}

	internal fun subtitleProfile(
		format: String,
		method: SubtitleDeliveryMethod
	) = SubtitleProfile().apply {
		this.format = format
		this.method = method
	}
}
