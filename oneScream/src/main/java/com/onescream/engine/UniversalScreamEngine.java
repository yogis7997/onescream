package com.onescream.engine;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Queue;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.onescream.info.BufferInfo;
import com.onescream.library.WavWriteMgr;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJCONST;
import com.uc.prjcmn.PRJFUNC;

/**
 * This is main Engine class for processing audio data to detect
 * <p/>
 * Created by Anwar Almojarkesh
 */
public class UniversalScreamEngine {

    protected final boolean _isLog = false;

    /**
     * Constants for setting audio device
     */
    public int RECORDER_SAMPLERATE = 44100;
    private final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    public final int FFT_SIZE = 4096;

    public static final float SENSITIVITY_OF_MICROPHONE = 1.0f;
    protected float fDeltaThreshold = 0.1f;

    /**
     * time duration to blinking camera flash light
     */
    public static final long FLASH_DURATION = 250L;

    /**
     * Variables for deciding Noise Environment
     */
    public static final int CONTINUES_BACKGROUND_NOISE_TIME = 10 * 10;
    public static final float BACKGROUND_NOISE_ROUGHNESS = 40.0f;
    int m_nNoiseSeqFrameCnt = 0;
    int m_nNormalSeqFrameCnt = 0;
    public boolean m_bInNoiseEnvironment = false;

    /**
     * Variables of Unviersal Engine
     */
    public static float SCREAM_LOUDNESS = 200.0f;
    public static float UNIVERSAL_THRESHOLD_DELTA = 0;

    public static final float FALSE_DETECTION_CORRECTION = 1.0f;
    public static final int UNIVERSAL_DETECT_PERIOD_FRAMES = 300;

    public int MAX_FREQS_CNT_TO_CHECK = 1;

    public float SCREAM_INSTABILITY_BANDWIDTH = -1; // 8 hz

    public int SCREAM_FREQ_MIN = 1000;
    public int SCREAM_FREQ_MAX = 2800;
    public int SCREAM_SOUND_TIME_MIN = 8; // frames
    public int SCREAM_SOUND_TIME_MAX = 25; // frames
    public int SCREAM_BREATH_TIME_MIN = 8; // frames
    public int SCREAM_BREATH_TIME_MAX = 25; // frames
    public int COUNTING_SCREAMS = 1;

    private int m_nUnivEngineFrameCnt = 0;
    private int m_nUnivEngineInvalidCnt = 0;
    private int m_nUnivEngineRepeatCnt = 0;

    private float[] m_maxFreqs;
    private boolean m_bInstability;

    public static final int ESCALATION_TIMEFRAME_MIN = 2;
    public static final int ESCALATION_TIMEFRAME_MAX = 5;

    /**
     * variables to detect
     */
    private boolean bRunning;

    /**
     * variables to get audio data to detect
     */
    private int minBufferSize = 0;
    private short[] buffer = null;
    private int bufferReadResult = 0;
    private AudioRecord audioRecord = null;
    private boolean aRecStarted = false;
    private int bufferSize = 2048;
    private float volume = 0;
    public FFTMgr fft = null;
    private float[] fftRealArray = null;
    public int nIdxFFTRealArray = 0;

    /**
     * minimum frequency to represent graphically
     */
    protected float minFreqToDraw = 1000;
    /**
     * maximum frequency to represent graphically
     */
    protected float maxFreqToDraw = 2800;
    public int minIdx = 0;
    public int maxIdx = 0;

    /**
     * variables to get audio data to detect
     */
    protected final int MAX_FREQ_CNT = 10;
    protected float[] maxVals = new float[MAX_FREQ_CNT];
    protected float[] maxFreqs = new float[MAX_FREQ_CNT];

    /**
     * variables for detecting and detected state
     */
    public boolean m_bDetected = false;
    private int m_nStepSinceDetected = 0;
    private int m_curSignal = -1;

    /**
     * variables for get escalation time
     */
    protected final int MAX_FRAME_CNT = 30;
    public int nIdxTimeFrame = 0;
    protected Queue<Float> maxVolumePerFrame = new CircularFifoQueue<Float>(
            MAX_FRAME_CNT);

    /**
     * recent audio data of 4 seconds to record scream when detected
     */
    public static final int LOOP_WAVE_DATA_BUF_CNT = 210;
    private ArrayList<BufferInfo> m_lstLoopWaveData = new ArrayList<BufferInfo>();
    private ArrayList<BufferInfo> m_lstLoopWaveDataSnap = new ArrayList<BufferInfo>();

    private Handler m_handlerForRecord = null;

    public UniversalScreamEngine() {


        resetFrameInfo();

        if (init())
            start();

        if (PRJCONST.isTesting) {
            SCREAM_LOUDNESS = 100.0f;

            MAX_FREQS_CNT_TO_CHECK = 1;

            SCREAM_INSTABILITY_BANDWIDTH = -1;

            COUNTING_SCREAMS = 1;
        }

    }

    public void resetFrameInfo() {
        m_nUnivEngineFrameCnt = 0;
        m_nUnivEngineInvalidCnt = 0;
        m_nUnivEngineRepeatCnt = 0;

        if (m_maxFreqs == null) {
            m_maxFreqs = new float[MAX_FREQS_CNT_TO_CHECK];
        }

        for (int i = 0; i < MAX_FREQS_CNT_TO_CHECK; i++) {
            m_maxFreqs[i] = 0;
        }

        m_bInstability = false;
    }

    private boolean init() {
        minBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        // if we are working with the android emulator, getMinBufferSize() does
        // not work and the only sampling rate we can use is 8000Hz
        if (minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            RECORDER_SAMPLERATE = 8000; // forced by the android emulator
            bufferSize = 2 << (int) (Math.log(RECORDER_SAMPLERATE)
                    / Math.log(2) - 1);
            // buffer size must be power of 2!!!
            // the buffer size determines the analysis frequency at:
            // RECORDER_SAMPLERATE/bufferSize
            // this might make trouble if there is not enough computation power
            // to record and analyze
            // a frequency. In the other hand, if the buffer size is too small
            // AudioRecord will not initialize
        } else
            bufferSize = minBufferSize;

        // maxFreqToDraw = MAX_FREQ;
        buffer = new short[bufferSize];
        m_lstLoopWaveData.clear();

        // use the mic with Auto Gain Control turned off!
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferSize);

        if ((audioRecord != null)
                && (audioRecord.getState() == AudioRecord.STATE_INITIALIZED)) {
            try {
                // this throws an exception with some combinations
                // of RECORDER_SAMPLERATE and bufferSize
                audioRecord.startRecording();
                aRecStarted = true;
            } catch (Exception e) {
                aRecStarted = false;
            }

            if (aRecStarted) {
                bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                // verify that is power of two
                // double dValue = Math.log(bufferReadResult) / Math.log(2);
                // if (dValue - (int) dValue != 0)
                // bufferReadResult = 1 << (int) (dValue + 1);

                fft = new FFTMgr(FFT_SIZE, RECORDER_SAMPLERATE);
                fftRealArray = new float[FFT_SIZE];

                minIdx = fft.freqToIndex(minFreqToDraw);
                maxIdx = fft.freqToIndex(maxFreqToDraw);

            } else {
                audioRecord = null;
            }
        }

        return aRecStarted;
    }

    public void Terminate() {

        try {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();

            }
        } catch (Exception e) {

        }

        stop();
    }

    public void running() {
        if (!bRunning)
            return;

        GlobalValues.sharedInstance()._frameCnt++;

        process();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                running();
            }
        }, 20);
    }

    public boolean isRunning() {
        return bRunning;
    }

    public void start() {

        bRunning = true;

        running();
    }

    public void stop() {
        bRunning = false;
    }

    private void process() {
        if (!aRecStarted || audioRecord == null)
            return;

        buffer = new short[bufferSize];
        bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
        if (bufferReadResult > 0) {
            BufferInfo bufInfo = new BufferInfo(buffer, bufferReadResult);
            m_lstLoopWaveData.add(bufInfo);
            if (m_lstLoopWaveData.size() > LOOP_WAVE_DATA_BUF_CNT)
                m_lstLoopWaveData.remove(0);
        }

        // Log.i("BufferReadResult", String.format("%d", bufferReadResult));
        int i = 0;

        while (true) {
            boolean bProcessable = false;
            for (; i < bufferReadResult; i++) {
                fftRealArray[nIdxFFTRealArray] = (float) buffer[i]
                        * SENSITIVITY_OF_MICROPHONE / 32768.0f;
                if (volume > Math.abs(fftRealArray[nIdxFFTRealArray]))
                    volume = Math.abs(fftRealArray[nIdxFFTRealArray]);

                nIdxFFTRealArray++;
                if (nIdxFFTRealArray >= FFT_SIZE) {
                    bProcessable = true;
                    break;
                }
            }

            if (!bProcessable)
                break;

            // contain volumes of latest twenty frames
            maxVolumePerFrame.add(volume);

            volume = 0;
            nIdxFFTRealArray = 0;

            volume = (float) (20.f * Math.log10(volume));

            // zero out first point (not touched by odd-length window)
            fftRealArray[0] = 0;
            fft.forward(fftRealArray);

            detectSounds();
        }
    }

    public boolean isDetecting() {
        return (GlobalValues.sharedInstance().m_bDetecting && !m_bDetected);
    }

    private void detectSounds() {
        float val = 0;
        float prevVal = 0;
        float predist = 0;
        float dist = 0;

        int idx = 0;

        for (int i = 0; i < MAX_FREQ_CNT; i++) {
            maxVals[i] = 0;
            maxFreqs[i] = 0;
        }

        double logDefault = Math.log10(FFT_SIZE);

        for (int i = minIdx; i <= maxIdx; i++) {
            if (_isLog)
                val = (float) (Math.log10(fft.getBand(i)) - logDefault) * 10 + 100;
            else
                val = fft.getBand(i);

            dist = val - prevVal;
            if (predist > 0 && dist < 0) { // one of maximum points
                if (prevVal > fDeltaThreshold) {
                    idx = 0;
                    for (; idx < MAX_FREQ_CNT; idx++) {
                        if (maxVals[idx] == 0 || prevVal > maxVals[idx])
                            break;
                    }

                    float fFreq = fft.indexToFreq(i - 1);

                    if (idx < MAX_FREQ_CNT) {
                        for (int j = MAX_FREQ_CNT - 1; j > idx; j--) {
                            if (maxVals[j - 1] == 0)
                                continue;
                            maxVals[j] = maxVals[j - 1];
                            maxFreqs[j] = maxFreqs[j - 1];
                        }
                        maxVals[idx] = prevVal;
                        maxFreqs[idx] = fFreq;
                    }
                }
            }

            predist = dist;
            prevVal = val;
        }

        String strLog = "";
        for (int i = 0; i < 2 /* MAX_FREQ_CNT */; i++) {
            strLog = strLog
                    + String.format("%.2f(%.5f) ", maxVals[i], maxFreqs[i]);
        }

        // Log.i("Freqs", strLog + "   Threshold:" + (UNIVERSAL_THRESHOLD +
        // UNIVERSAL_THRESHOLD_DELTA));

        val = (float) Math.log10(maxVals[0]);

        if (isDetecting()) {
            float evaluation_val = maxVals[0];
            // evaluation_val = average_val;
            if (evaluation_val > BACKGROUND_NOISE_ROUGHNESS) {
                m_nNoiseSeqFrameCnt++;
                m_nNormalSeqFrameCnt = 0;
            } else {
                m_nNormalSeqFrameCnt++;
                m_nNoiseSeqFrameCnt = 0;
            }

            if (!m_bInNoiseEnvironment) {
                if (m_nNoiseSeqFrameCnt >= CONTINUES_BACKGROUND_NOISE_TIME) {
                    m_bInNoiseEnvironment = true;
                    if (GlobalValues.sharedInstance()._myService != null
                            && GlobalValues.sharedInstance()._myService.m_handler != null) {
                        GlobalValues.sharedInstance()._myService.m_handler
                                .sendEmptyMessage(GlobalValues.COMMAND_NOTIFY_NOISY_ENV);
                    }
                }
            } else {
                if (m_nNormalSeqFrameCnt >= CONTINUES_BACKGROUND_NOISE_TIME) {
                    m_bInNoiseEnvironment = false;
                    if (GlobalValues.sharedInstance()._myService != null
                            && GlobalValues.sharedInstance()._myService.m_handler != null) {
                        GlobalValues.sharedInstance()._myService.m_handler
                                .sendEmptyMessage(GlobalValues.COMMAND_ENABLE_DETECT);
                    }
                }
            }
        } else {
            m_nNoiseSeqFrameCnt = 0;
            m_nNormalSeqFrameCnt = 0;
        }

        float nThreshold = SCREAM_LOUDNESS + UNIVERSAL_THRESHOLD_DELTA;
        boolean bCandidate = true;
        for (int i = 0; i < MAX_FREQS_CNT_TO_CHECK; i++) {
            if (!(maxFreqs[i] >= SCREAM_FREQ_MIN
                    && maxFreqs[i] <= SCREAM_FREQ_MAX && maxVals[i] >= nThreshold)) {
                bCandidate = false;
                break;
            }
        }

        if (bCandidate) {
            m_nUnivEngineInvalidCnt = 0;

            // check instability of Top Frequencies
            float[] tmpMaxFreq = new float[MAX_FREQS_CNT_TO_CHECK];
            for (int i = 0; i < MAX_FREQS_CNT_TO_CHECK; i++) {
                tmpMaxFreq[i] = maxFreqs[i];
            }

            for (int i = 0; i < MAX_FREQS_CNT_TO_CHECK - 1; i++) {
                for (int j = i + 1; j < MAX_FREQS_CNT_TO_CHECK; j++) {
                    if (tmpMaxFreq[i] > tmpMaxFreq[j]) {
                        float fTemp = tmpMaxFreq[i];
                        tmpMaxFreq[i] = tmpMaxFreq[j];
                        tmpMaxFreq[j] = fTemp;
                    }
                }
            }

            if (m_nUnivEngineFrameCnt > 0) {
                boolean bInstable = true;
                for (int i = 0; i < MAX_FREQS_CNT_TO_CHECK; i++) {
                    if (Math.abs(tmpMaxFreq[i] - m_maxFreqs[i]) < SCREAM_INSTABILITY_BANDWIDTH) {
                        bInstable = false;
                    }

                    m_maxFreqs[i] = tmpMaxFreq[i];
                }

                if (bInstable || SCREAM_INSTABILITY_BANDWIDTH < 0) {
                    m_bInstability = true;
                }
            } else {
                m_bInstability = false;
            }

            m_nUnivEngineFrameCnt++;
            if (m_nUnivEngineFrameCnt == SCREAM_SOUND_TIME_MIN) {
                m_nUnivEngineRepeatCnt++;
                // m_nBabyCryingFrameCnt[type] = 0;
                if (m_nUnivEngineRepeatCnt >= COUNTING_SCREAMS) {
                    if (m_bInstability) {
                        m_bDetected = true;
                        m_curSignal = PRJFUNC
                                .ConvSignalToInt(PRJFUNC.Signal.ONE_SCREAM);
                        PRJFUNC.DETECTED_NUMBER = 0;
                        PRJFUNC.DETECTED_TYPE = m_curSignal;
                    }
                }
            } else if (m_nUnivEngineFrameCnt >= SCREAM_SOUND_TIME_MAX) {
                m_nUnivEngineRepeatCnt = 0;
            } else if (m_nUnivEngineFrameCnt == UNIVERSAL_DETECT_PERIOD_FRAMES) {
                m_bDetected = true;
                m_curSignal = PRJFUNC
                        .ConvSignalToInt(PRJFUNC.Signal.ONE_SCREAM);
                PRJFUNC.DETECTED_NUMBER = 0;
                PRJFUNC.DETECTED_TYPE = m_curSignal;
            }
        } else {
            m_nUnivEngineInvalidCnt++;
            if (m_nUnivEngineInvalidCnt >= SCREAM_BREATH_TIME_MIN
                    && m_nUnivEngineFrameCnt > 0) {
                m_nUnivEngineFrameCnt = 0;

                // Check once more when a scream is finished.
                if (!m_bInstability || !checkWithEscalationTime()) {
                    m_nUnivEngineRepeatCnt = 0;
                }

                m_bInstability = false;
            }

            if (m_nUnivEngineInvalidCnt > SCREAM_BREATH_TIME_MAX
                    && m_nUnivEngineRepeatCnt > 0) {
                m_nUnivEngineRepeatCnt = 0;
            }
        }

        if (m_nUnivEngineInvalidCnt > 5000) {
            m_nUnivEngineInvalidCnt = 1000;
        }

        Log.i("Screaming", String.format(
                "max_val=%.3f,   max_val2=%.3f,       frame_cnt=%d,       invalid_cnt=%d,          repeatcnt=%d,          maxFreq=%.3f,          maxFreq2=%.3f",
                maxVals[0],maxVals[1], m_nUnivEngineFrameCnt, m_nUnivEngineInvalidCnt,
                m_nUnivEngineRepeatCnt,maxFreqs[0],maxFreqs[1]));
//		Log.e("YogiScream", String.format(
//				"max_val=%.3f       frame_cnt=%d,       invalid_cnt=%d,          repeatcnt=%d",
//				maxVals[0], m_nUnivEngineFrameCnt, m_nUnivEngineInvalidCnt,
//				m_nUnivEngineRepeatCnt));
        if (m_bDetected) {

            GlobalValues.sharedInstance().m_strDetectedAddress = null;
            GlobalValues.sharedInstance().m_strDetectedLocation = null;
            GlobalValues.sharedInstance().m_strDetectedObjectId = null;

            resetFrameInfo();

            if (GlobalValues.sharedInstance()._myService != null
                    && GlobalValues.sharedInstance()._myService.m_handler != null) {

                GlobalValues.sharedInstance().m_bDetectionPhone = true;

                Message msg = new Message();
                // msg.what = GlobalValues.COMMAND_OPEN_ACITIVITY;
                msg.what = GlobalValues.COMMAND_PUSH_NOTIFICATION;
                msg.obj = m_curSignal;
                GlobalValues.sharedInstance()._myService.m_handler
                        .sendMessage(msg);

                String strFileWave = PRJFUNC.getCurHistorySoundFileName();
                saveToWaveFile(strFileWave);
            }

            m_nStepSinceDetected = 300;
        }

        if (m_bDetected) {
            m_nStepSinceDetected--;
            if (m_nStepSinceDetected == 0)
                m_bDetected = false;
        }

    }

    /**
     * Check scream with Escalation time
     *
     * @return
     */
    private boolean checkWithEscalationTime() {
        if (PRJCONST.isTesting) {
            SCREAM_LOUDNESS = 200.0f;

            MAX_FREQS_CNT_TO_CHECK = 1;

            SCREAM_INSTABILITY_BANDWIDTH = -1;

            COUNTING_SCREAMS = 1;
            return true;
        }


        Float[] volumeArray = maxVolumePerFrame
                .toArray(new Float[MAX_FRAME_CNT]);
        // (float[])maxVolumePerFrame.toArray();
        int thresoldIndex = 0;

        ArrayList<Integer> minPosArray = new ArrayList<Integer>();
        ArrayList<Integer> maxPosArray = new ArrayList<Integer>();

        boolean bPrevIsInc = volumeArray[1].floatValue() > volumeArray[0]
                .floatValue();

        for (int i = 1; i < MAX_FRAME_CNT - 1; i++) {
            boolean bInc = volumeArray[i + 1].floatValue() > volumeArray[i].floatValue();
            if (bPrevIsInc && !bInc) {
                maxPosArray.add(i);
            } else if (!bPrevIsInc && bInc) {
                minPosArray.add(i);
            }

            bPrevIsInc = bInc;
        }

        int nMinPos = 0;
        int nMaxPos = 0;

        int nScreamStartPos = MAX_FRAME_CNT - 1 - m_nUnivEngineFrameCnt;
        if (nScreamStartPos < 0) {
            nScreamStartPos = 0;
        }

        for (int i = 0; i < minPosArray.size(); i++) {
            nMinPos = minPosArray.get(i);
            if (nMinPos >= nScreamStartPos) {
                break;
            }
        }

        for (int i = 0; i < maxPosArray.size(); i++) {
            nMaxPos = maxPosArray.get(i);
            if (nMaxPos >= nScreamStartPos && nMaxPos > nMinPos) {
                break;
            }
        }

        if ((nMaxPos - nMinPos) >= ESCALATION_TIMEFRAME_MIN && (nMaxPos - nMinPos) <= ESCALATION_TIMEFRAME_MAX) {
            return true;
        }

        return false;
    }

    public void clearDetectedStatus() {
        m_bDetected = false;
        m_nStepSinceDetected = 0;
    }

    /**
     * Write current recorded data to wave file for about 3 seconds And, this
     * function must be called in same thread of process() function
     */
    private void saveToWaveFile(final String p_strFileName) {
        m_lstLoopWaveDataSnap.clear();
        for (int i = 0; i < m_lstLoopWaveData.size(); i++) {
            BufferInfo bufInfo = m_lstLoopWaveData.get(i);
            m_lstLoopWaveDataSnap.add(bufInfo);
        }

        new Thread() {
            public void run() {
                int nTotalSize = 0;

                for (int i = 0; i < m_lstLoopWaveDataSnap.size(); i++) {
                    BufferInfo bufInfo = m_lstLoopWaveDataSnap.get(i);
                    nTotalSize += bufInfo.real_length;
                }

                if (nTotalSize == 0)
                    return;

                File dir = new File(PRJFUNC.getHistorySoundDir(GlobalValues
                        .sharedInstance()._myService));
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String strFilePath = PRJFUNC.getHistorySoundDir(GlobalValues
                        .sharedInstance()._myService) + "/" + p_strFileName;
                WavWriteMgr wavFileMgr = new WavWriteMgr(strFilePath);
                wavFileMgr.setWaveInfo(RECORDER_SAMPLERATE, 16, 1);
                boolean bResult = wavFileMgr.startWriteFile(nTotalSize * 2/*
                                                                         * sizeof(
																		 * short
																		 * )
																		 */);
                if (!bResult)
                    return;

                for (int i = 0; i < m_lstLoopWaveDataSnap.size(); i++) {
                    BufferInfo bufInfo = m_lstLoopWaveDataSnap.get(i);
                    bResult = wavFileMgr.appendData(bufInfo.buf,
                            bufInfo.real_length);

                    if (!bResult)
                        break;
                }

                bResult = wavFileMgr.finish();

                if (bResult) {
                    saveHistoryToParse(strFilePath);
                }

            }
        }.start();
    }

    /**
     * Save file to Parse.com
     *
     * @param p_strFilePath
     * @return
     */
    private boolean saveHistoryToParse(String p_strFilePath) {

        try {
            FileInputStream fis = new FileInputStream(p_strFilePath);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            ParseFile file = new ParseFile("scream_new.wav", buffer,
                    "audio/x-wav");
            file.save();

            ParseUser user = ParseUser.getCurrentUser();

            boolean bLocationSaved = false;

            ParseObject detect = ParseObject.create("detection_history");
            detect.put("scream_file", file);
            detect.put("userObjectId", user.getObjectId());
            detect.put("userEmail", user.getEmail());
            detect.put("device_type", "android");
            if (user.getString("postcode") != null) {
                detect.put("postcode", user.getString("postcode"));
            }

            detect.put("phone", user.getString("phone"));
            detect.put(
                    "fullname",
                    user.getString("first_name") + " "
                            + user.getString("last_name"));
            if (!PRJFUNC
                    .isNullOrEmpty(GlobalValues.sharedInstance().m_strDetectedAddress)) {
                detect.put("location",
                        GlobalValues.sharedInstance().m_strDetectedLocation);
                detect.put("address",
                        GlobalValues.sharedInstance().m_strDetectedAddress);
                GlobalValues.sharedInstance().m_strDetectedAddress = null;
                GlobalValues.sharedInstance().m_strDetectedLocation = null;
                bLocationSaved = true;
            }

            detect.save();

            if (!bLocationSaved) {
                GlobalValues.sharedInstance().m_strDetectedObjectId = detect
                        .getObjectId();
            }
        } catch (Exception e) {
            return false;
        }

        return true;

    }

}
