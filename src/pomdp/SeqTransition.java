package pomdp;

public class SeqTransition extends Transition {
	// =====================
	// Enumerates
	// =====================
	public enum TransitionType {
		// GOAL: ワークフロー終了による遷移
		// BUNKRUPT: 予算切れによる遷移
		// TRANSITION: 途中遷移
		GOAL, BUNKRUPT, TRANSITION
	}

	// =====================
	// Fields
	// =====================
	TransitionType mType;

	// =====================
	// Constructors
	// =====================
	public SeqTransition(State pPrevState, Action pAction, State pNextState, double pProb, TransitionType pType) {
		super(pPrevState, pAction, pNextState, pProb);
		mType = pType;
	}

	// =====================
	// Methods
	// =====================
	@Override
	protected void calcReward() {
		// TODO: 報酬関数を作る事
	}

}
