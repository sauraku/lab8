import sun.net.www.http.Hurryable;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by skwow on 11/6/2016.
 * Saurabh Kumar 2015088
 * Vanshit Gupta 2015186
 */
public class Consumer3 implements Consumer,Runnable {
    private ArrayList<Producer> producers= new ArrayList<>();

    private float avTemp;
    private float avHumidity;
    private float avRainfall;
    private float[] temp;
    private float[] humidity;
    private float[] rainfall;


    private ReentrantLock myLock = new ReentrantLock();
    private Condition myCondition = myLock.newCondition();
    @Override
    public void receiveTempData(float[] temp) {
        this.temp=temp;

    }

    @Override
    public void receiveHumidityData(float[] humidity) {
        this.humidity=humidity;

    }

    @Override
    public void receiveRainfallData(float[] rainfall) {
        this.rainfall=rainfall;


    }

    @Override
    public boolean signal() {
        if( myLock.tryLock()) {
            try {
                myCondition.signalAll();
            } finally {
                myLock.unlock();
                return true;
            }
        }else
        {
            return false;
        }
    }

    @Override
    public void dostuff() {
        avTemp =0;
        avHumidity =0;
        avRainfall =0;
        for(int i=0;i<100;i++) {avTemp+=temp[i];}
        for(int i=0;i<100;i++) {avHumidity+= humidity[i];}
        for(int i=0;i<100;i++) {avRainfall+=rainfall[i];}
        for(int i=0;i<producers.size();i++)
        {
            producers.get(i).receiveAvData(avTemp/100,avHumidity/100,avRainfall/100);
        }
        temp=null;
        humidity=null;
        rainfall=null;
    }

    @Override
    public void addProducer(Producer p) {
        producers.add(p);
    }

    @Override
    public void run() {
        myLock.lock();
        while(true)
        {
            try
            {
                Thread.sleep((long) (Math.random()*2000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i=0;i<producers.size();i++) {
                if (!producers.get(i).signal()) {
                }
            }
            try
            {
                while(temp==null || humidity==null || rainfall==null) {
                    myCondition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            dostuff();
            for(int i=0;i<producers.size();i++)
            {
                if(!producers.get(i).signal()){
                    /* try {
                        myCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                }//wait};
            }
        }
    }
}
