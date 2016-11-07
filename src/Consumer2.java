import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by skwow on 11/6/2016.
 * Saurabh Kumar 2015088
 * Vanshit Gupta 2015186
 */
public class Consumer2 implements Consumer,Runnable {
    private ArrayList<Producer> producers= new ArrayList<>();
    private float[] temp;
    private float[] humidity;
    private float[] rainfall;

    private float maxTemp;
    private float maxHumidity;
    private float maxRainfall;


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
        maxTemp =-100000;
        maxHumidity =-100000;
        maxRainfall =-100000;
        for(int i=0;i<100;i++) {if(temp[i]>maxTemp){maxTemp=temp[i];}}
        for(int i=0;i<100;i++) {if(humidity[i]>maxHumidity){maxHumidity=humidity[i];}}
        for(int i=0;i<100;i++) {if(rainfall[i]>maxRainfall){maxRainfall=rainfall[i];}}
        for(int i=0;i<producers.size();i++)
        {
            producers.get(i).receiveMaxData(maxTemp,maxHumidity,maxRainfall);
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
                }
            }
        }
    }
}
