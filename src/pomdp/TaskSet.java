package pomdp;

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
	public int mDivNum; // 分割数
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
	// Methods
	// ===================

	/**
	 * サブタスクを取得
	 */
	public Subtask getSubtask(int pIndex) {
		return mTasks.get(pIndex);
	}

	@Override
	public String toString() {
		return "Tasks(n:" + mDivNum + ", tasks=" + mTasks + ")";
	}

}
