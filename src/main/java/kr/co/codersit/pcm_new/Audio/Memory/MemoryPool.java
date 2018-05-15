package kr.co.codersit.pcm_new.Audio.Memory;

import java.util.ArrayList;

public class MemoryPool  {
    private ArrayList<byte[]> mBytes;

    private MemoryPool() {
        mBytes = new ArrayList<>();
    }

    public static MemoryPool getInstance() {
        return LazyHolder.INSTANCE;
    }

    public byte[] allocate() {
        byte[] tmpData = null;
        synchronized (mBytes) {
            if ( mBytes.size() > 0 ) {
                tmpData = mBytes.remove(0);
            }
            else {
                tmpData = new byte[1024];
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
