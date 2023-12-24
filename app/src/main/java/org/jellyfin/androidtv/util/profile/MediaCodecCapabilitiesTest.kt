package org.jellyfin.androidtv.util.profile

import android.media.MediaCodecInfo.CodecProfileLevel
import android.media.MediaCodecList
import android.media.MediaFormat
import android.os.Build
import timber.log.Timber

class MediaCodecCapabilitiesTest {
	private val mediaCodecList by lazy { MediaCodecList(MediaCodecList.REGULAR_CODECS) }

	fun supportsAv1(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
		hasCodecForMime(MediaFormat.MIMETYPE_VIDEO_AV1)

	fun supportsAv1Main10(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
		hasDecoder(
			MediaFormat.MIMETYPE_VIDEO_AV1,
			CodecProfileLevel.AV1ProfileMain10,
			CodecProfileLevel.AV1Level4
		)

	fun supportsHevc(): Boolean = hasCodecForMime(MediaFormat.MIMETYPE_VIDEO_HEVC)

	fun supportsHevcMain10(): Boolean = hasDecoder(
		MediaFormat.MIMETYPE_VIDEO_HEVC,
		CodecProfileLevel.HEVCProfileMain10,
		CodecProfileLevel.HEVCMainTierLevel4
	)

	fun supportsAvcHigh10(): Boolean = hasDecoder(
		MediaFormat.MIMETYPE_VIDEO_AVC,
		CodecProfileLevel.AVCProfileHigh10,
		CodecProfileLevel.AVCLevel4
	)

	private fun hasDecoder(mime: String, profile: Int, level: Int): Boolean {
		for (info in mediaCodecList.codecInfos) {
			if (info.isEncoder) continue

			try {
				val capabilities = info.getCapabilitiesForType(mime)
				for (profileLevel in capabilities.profileLevels) {
					if (profileLevel.profile != profile) continue

					// H.263 levels are not completely ordered:
					// Level45 support only implies Level10 support
					if (mime.equals(MediaFormat.MIMETYPE_VIDEO_H263, ignoreCase = true)) {
						if (profileLevel.level != level && profileLevel.level == CodecProfileLevel.H263Level45 && level > CodecProfileLevel.H263Level10) {
							continue
						}
					}

					if (profileLevel.level >= level) return true
				}
			} catch (e: IllegalArgumentException) {
				Timber.w(e)
			}
		}

		return false
	}

	private fun hasCodecForMime(mime: String): Boolean {
		for (info in mediaCodecList.codecInfos) {
			if (info.isEncoder) continue

			if (info.supportedTypes.any { it.equals(mime, ignoreCase = true) }) {
				Timber.i("found codec %s for mime %s", info.name, mime)
				return true
			}
		}

		return false
	}
}
