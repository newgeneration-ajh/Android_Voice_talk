package kr.co.codersit.pcm_new.Audio.Memory;

import java.util.ArrayList;

public class MemoryPool  {
    private ArrayList<byte[]> mBytes;
    private int mBufferSize = 1024;

    private MemoryPool() {
        mBytes = new ArrayList<>();
    }

    public static MemoryPool getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void setBufferSize ( int size ) {
        mBufferSize = size;
    }

    public byte[] allocate() {
        byte[] tmpData = null;
        synchronized (mBytes) {
            if ( mBytes.size() > 0 ) {
                tmpData = mBytes.remove(0);
                if ( tmpData.length != mBufferSize ) {
                    tmpData = new byte[mBufferSize];
                }
            }
            else {
                tmpData = new byte[mBufferSize];
            }
        }
        return tmpData;
    }

    public synchronized void returnMemory ( byte[] data ) {
        mBytes.add(data);
    }

    private static class LazyHolder {
        private static final MemoryPool INSTANCE = new MemoryPool();
    }
}
