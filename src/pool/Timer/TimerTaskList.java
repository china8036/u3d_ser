package pool.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


public class TimerTaskList extends TimerTask {

	private List<TimerListener> tList = new ArrayList<TimerListener>();

	public TimerTaskList(TimerListener tl) {
		this.tList.add(tl);
	}

	public void addListener(TimerListener tl) {
		if (this.tList.contains(tl)) {
			return;
		}
		this.tList.add(tl);
	}

	@Override
	public void run() {
		for (TimerListener nl : tList) {
			nl.onTimer();
		}
		// TODO Auto-generated method stub

	}

}
