package de.mas.jnustool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Progress {
	private AtomicLong total = new AtomicLong();
	private AtomicLong current  = new AtomicLong();
	private ProgressUpdateListener progressUpdateListener = null;
	List<Progress> children = new ArrayList<>();
	Progress father = null;
	private AtomicLong totalChildren = new AtomicLong();
	private AtomicLong currentChildren = new AtomicLong();

	
	public long getTotalOfSingle() {
		return total.get();
	}
	
	private void setTotalOfSingle(long total) {
		this.total.set(total);
	}

	public long getCurrentOfSingle() {
		return current.get();
	}

	private void setCurrent(long current) {
		this.current.set(current);	
		update();
	}
	
	public void addTotal(long i) {		
		setTotalOfSingle(getTotalOfSingle() + i);
		if(father != null) father.addTotalChildren(i);
		
	}

	private void addCurrentChildren(long i) {
		if(father != null) father.addCurrentChildren(i);
		this.currentChildren.addAndGet(i);	
		
	}
	
	private void addTotalChildren(long i) {
		if(father != null){
			father.addTotalChildren(i);
		}
		this.totalChildren.addAndGet(i);
	}

	public void addCurrent(int i) {
		if(this.current.get() + i > getTotalOfSingle()){
			setCurrent(getTotalOfSingle());
		}else{
			setCurrent(getCurrentOfSingle() + i);
			if(father != null) father.addCurrentChildren(i);
		}
	}

	private void update() {		
		if(father != null) father.update();
		
		if(progressUpdateListener !=  null){
				progressUpdateListener.updatePerformed(this);			
		}
	}

	public void add(Progress progress) {
		progress.setFather(this);
		addChildrenTotal(progress.getTotalOfSingle());
		children.add(progress);		
	}

	private void addChildrenTotal(long totalOfSingle) {
		this.totalChildren.addAndGet(totalOfSingle);		
	}

	private void setFather(Progress progressListener) {
		this.father =  progressListener;		
	}

	public long getCurrent() {
		return this.currentChildren.get() + this.current.get();
	}

	
	public long getTotal() {		
		return this.totalChildren.get() + this.total.get();
	}
	
	
	public void setTotal(long total) {		
		this.total.set(total);
	}

	
	public void setProgressUpdateListener(ProgressUpdateListener progressUpdateListener) {
		this.progressUpdateListener = progressUpdateListener;
	}

	public void clear() {
		current.set(0);
		currentChildren.set(0);
		total.set(0);
		totalChildren.set(0);
		father = null;
		children = new ArrayList<>();
	}

	public int statusInPercent() {
		return (int) ((getCurrent()*1.0)/(getTotal()*1.0)*100);
	}

	public void finish() {
		addCurrent((int) getTotalOfSingle());
	}
	
	private BooleanProperty inprogress;
        public BooleanProperty inprogressProperty(){
            if(inprogress==null){
                inprogress=new SimpleBooleanProperty(Progress.class,"inprogress");
            }
            return inprogress;
        }
	public void operationStart() {
		inprogressProperty().set(true);
	}
	public void operationFinish() {
		inprogressProperty().set(false);
	}
	
	public boolean isInProgress(){
		return inprogressProperty().get();
	}

	public void resetCurrent() {
		while(getCurrentOfSingle() > 0){
			addCurrent((int)getCurrentOfSingle() * (-1));
		}
	}
	
}
