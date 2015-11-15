package pomdp;

import java.util.Collection;
import java.util.HashMap;
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
	private int mDivNum; // 分割数
	public Map<Integer, Subtask> mTasks;

	// ===================
	// Constructors
	// ===================
	public TaskSet() {
		mDivNum = 0;
		mTasks = new HashMap<Integer, Subtask>();
	}

	public TaskSet(int pDivNum) {
		mDivNum = pDivNum;
		mTasks = new HashMap<Integer, Subtask>();
	}

	// ===================
	// Getters & Setters
	// ===================
	public int getDivNum() {
		return mDivNum;
	}

	public void setDivNum(int pNum) {
		mDivNum = pNum;
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
		return "Tasks(n:" + mDivNum + ", tasks=" + mTasks + ")";
	}

}
