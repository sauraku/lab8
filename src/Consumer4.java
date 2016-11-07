import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by skwow on 11/7/2016.
 * Saurabh Kumar 2015088
 * Vanshit Gupta 2015186
 */
public class Consumer4 implements Consumer,Runnable {
    private ArrayList<Producer> producers= new ArrayList<>();

    private float nextTemp;
    private float nextHumidity;
    private float nextRainfall;
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
        nextTemp=0;
        nextHumidity=0;
        nextRainfall=0;
        for(int i=1;i<100;i++)
        {
            nextTemp+=temp[i]-temp[i-1];
            nextHumidity+=humidity[i]-humidity[i-1];
            nextRainfall+=rainfall[i]-rainfall[i-1];
        }
        for(int i=0;i<producers.size();i++)
        {
            producers.get(i).receiveNextData(temp[99]+nextTemp/100,humidity[99]+nextHumidity/100,rainfall[99]+nextRainfall/100);
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
