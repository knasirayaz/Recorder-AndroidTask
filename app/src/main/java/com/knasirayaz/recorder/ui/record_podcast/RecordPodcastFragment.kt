package com.knasirayaz.recorder.ui.record_podcast

import android.Manifest
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.filepickersample.enumeration.FileType
import com.filepickersample.utils.FileUtil
import com.knasirayaz.recorder.R
import com.knasirayaz.recorder.base.BaseFragment
import com.knasirayaz.recorder.databinding.FragmentRecordpodcastBinding
import com.knasirayaz.recorder.utils.DateTimeHelpers
import com.knasirayaz.recorder.utils.hide
import com.knasirayaz.recorder.utils.show
import com.knasirayaz.recorder.utils.snackbar
import kotlinx.coroutines.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.io.File
import java.io.IOException


enum class RecordingStates {
    IDLE, START, PAUSE, RESUME
}

class RecordPodcastFragment :
    BaseFragment<FragmentRecordpodcastBinding>(R.layout.fragment_recordpodcast) {


    var mMediaRecorder: MediaRecorder? = null
    var isRecording = false


    var mRecordingFilePath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                if (isRecording) {
                    val currentMaxAmplitude = mMediaRecorder?.maxAmplitude
                    currentMaxAmplitude?.let {
                        viewBinding.audioRecordView.update(it)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUiConfiguration(RecordingStates.IDLE)
        initializeMediaRecorder()
        listeners()

    }

    private var mIdleRecordListener =
        View.OnClickListener { setUiConfiguration(RecordingStates.START) }
    private var mStartRecordListener =
        View.OnClickListener { setUiConfiguration(RecordingStates.PAUSE) }
    private var mPauseRecordListener =
        View.OnClickListener { setUiConfiguration(RecordingStates.RESUME) }
    private var mResumeRecordListener =
        View.OnClickListener { setUiConfiguration(RecordingStates.PAUSE) }


    private fun listeners() {
        viewBinding.btnUndo.setOnClickListener {
            mRecordingFilePath?.let { it1 -> File(it1).delete() }
            mRecordingFilePath = null
            setUiConfiguration(RecordingStates.IDLE)
        }
        viewBinding.btnPlay.setOnClickListener {
            setUiConfiguration(RecordingStates.RESUME)
        }

        viewBinding.btnSave.setOnClickListener {
            if(mRecordingFilePath != null) {
                SaveRecordingBottomsheet() {
                    val mRenamedFile = File(FileUtil.getAudioDirectory(requireContext()), "$it.wav")
                    val mRenamedFileFrom = File(
                        FileUtil.getAudioDirectory(requireContext()), File(
                            mRecordingFilePath.toString()
                        ).name
                    )
                    mRenamedFileFrom.renameTo(mRenamedFile)
                    resetEverything()
                    findNavController().popBackStack()
                }.show(requireActivity())
            }else{
                snackbar(requireView(), getString(R.string.error_no_recording_found))
            }
        }
    }

    private fun resetEverything() {
        mMediaRecorder?.reset()
        viewBinding.chronometer.reset()
        viewBinding.audioRecordView.recreate()
    }

    private fun initializeMediaRecorder() {
        mMediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(requireContext())
        } else {
            MediaRecorder()
        }
    }

    private fun setUiConfiguration(recordState: RecordingStates) {
        viewBinding.btnRecord.tag = recordState

        when (recordState) {
            RecordingStates.IDLE -> {
                setTitle(getString(R.string.start_recording))

                viewBinding.btnRecord.setOnClickListener(mIdleRecordListener)

                viewBinding.chronometer.show()
                viewBinding.tvSaveInfo.hide()
                viewBinding.btnPlay.hide()
                viewBinding.llSave.hide()
                viewBinding.llUndo.hide()
                viewBinding.tvOfBtnRecord.text = getString(R.string.start)
                resetEverything()
            }

            RecordingStates.START -> {
                if (isPermissionGranted()) {
                    setTitle(getString(R.string.recording))
                    viewBinding.btnRecord.setOnClickListener(mStartRecordListener)

                    viewBinding.chronometer.show()
                    viewBinding.btnPlay.hide()
                    viewBinding.tvSaveInfo.hide()
                    viewBinding.llSave.hide()
                    viewBinding.llUndo.hide()
                    viewBinding.tvOfBtnRecord.text = getString(R.string.stop)

                    startRecording()
                }
            }

            RecordingStates.PAUSE -> {
                setTitle(getString(R.string.title_preview_your_audio))

                viewBinding.btnRecord.setOnClickListener(mPauseRecordListener)

                viewBinding.chronometer.hide()
                viewBinding.tvSaveInfo.show()
                viewBinding.btnPlay.show()
                viewBinding.llSave.show()
                viewBinding.llUndo.show()
                viewBinding.tvOfBtnRecord.text = getString(R.string.continu)

                pauseRecording()
            }

            RecordingStates.RESUME -> {
                viewBinding.btnRecord.setOnClickListener(mResumeRecordListener)

                viewBinding.chronometer.show()
                viewBinding.tvSaveInfo.hide()
                viewBinding.btnPlay.hide()
                viewBinding.llSave.hide()
                viewBinding.llUndo.hide()
                viewBinding.tvOfBtnRecord.text = getString(R.string.stop)

                resumeRecording()
            }
        }

    }

    private fun resumeRecording() {
        isRecording = true
        mMediaRecorder?.resume()
        viewBinding.chronometer.start()


    }

    private fun pauseRecording() {
        isRecording = false
        mMediaRecorder?.pause()
        viewBinding.chronometer.stop()
    }


    private fun startRecording() {
        configureRecorder()
        try {
            mMediaRecorder?.prepare()
            mMediaRecorder?.start()
            isRecording = true
            viewBinding.chronometer.start()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun isPermissionGranted(): Boolean {
        val isPermission =
            EasyPermissions.hasPermissions(requireContext(), Manifest.permission.RECORD_AUDIO) &&
                    EasyPermissions.hasPermissions(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) &&
                    EasyPermissions.hasPermissions(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

        return if (isPermission) {
            true
        } else {
            val builder = PermissionRequest.Builder(
                this@RecordPodcastFragment, 210, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).build()
            EasyPermissions.requestPermissions(builder)
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 210 && grantResults[0] != -1 && grantResults[1] != -1 && grantResults[2] != -1) {
            setUiConfiguration(RecordingStates.START)
        }
    }

    private fun configureRecorder() {
        mRecordingFilePath = FileUtil.createNewFile(
            requireContext(),
            DateTimeHelpers.getTodayDate() + ".wav",
            FileType.AUDIO
        ).path

        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder?.setAudioChannels(1);
        mMediaRecorder?.setAudioEncodingBitRate(128000);
        mMediaRecorder?.setAudioSamplingRate(44100);
        mMediaRecorder?.setOutputFile(mRecordingFilePath)
    }

}