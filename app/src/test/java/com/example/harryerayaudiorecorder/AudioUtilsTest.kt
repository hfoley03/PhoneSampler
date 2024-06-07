
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.example.harryerayaudiorecorder.AudioUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK])
class AudioUtilsTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        mockkStatic(ContextCompat::class)
    }
    @Test
    fun hasRecordAudioPermissionEeturnsTrueWhenPermissionIsGranted() {
        every { ContextCompat.checkSelfPermission(any(), android.Manifest.permission.RECORD_AUDIO) } returns PackageManager.PERMISSION_GRANTED
        val result = AudioUtils.hasRecordAudioPermission(context)
        assert(result) { "Expected permission" }
    }
    @Test
    fun hasRecordAudioPermissionEeturnsFalseeWhenPermissionIsNotGranted() {
        every { ContextCompat.checkSelfPermission(any(), android.Manifest.permission.RECORD_AUDIO) } returns PackageManager.PERMISSION_DENIED
        val result = AudioUtils.hasRecordAudioPermission(context)
        assert(!result) { "Expected no permission" }
    }
}
