package pomdp;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * タスククラス．サブタスク集合を持つ
 * 
 * @author y-itoh
 *
 */
public class TaskSet {
	// ===================
	// Fields
	// ===================
	private int mSubtaskNum; // サブタスク数
	private Map<Integer, Subtask> mTasks;

	// ===================
	// Constructors
	// ===================
	public TaskSet() {
		mSubtaskNum = 0;
		mTasks = new LinkedHashMap<Integer, Subtask>();
	}

	public TaskSet(int pSubtaskNum) {
		mSubtaskNum = pSubtaskNum;
		mTasks = new LinkedHashMap<Integer, Subtask>();
	}

	// ===================
	// Getters & Setters
	// ===================
	public Map<Integer, Subtask> getTasks() {
		return mTasks;
	}

	public int getSubtaskNum() {
		return mSubtaskNum;
	}

	public void setDivNum(int pNum) {
		mSubtaskNum = pNum;
	}

	// ===================
	// Methods
	// ===================

	/**
	 * タスクを追加
	 */
	public void putSubtask(int pIndex, Subtask pSubtask) {
		mTasks.put(pIndex, pSubtask);
	}

	/**
	 * サブタスクを取得
	 */
	public Subtask getSubtask(int pIndex) {
		return mTasks.get(pIndex);
	}

	/**
	 * 全てのサブタスクを取得
	 */

	public Collection<Subtask> getSubtasks() {
		return mTasks.values();
	}

	@Override
	public String toString() {
		return "Tasks(n:" + mSubtaskNum + ", tasks=" + mTasks + ")";
	}

}
