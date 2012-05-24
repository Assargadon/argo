package net.kiborgov.argo.android.client.interfaces;

public interface PhoneEventListener {

	void onIncomingCall(SessionContext context, Contact contact);

	void onIncomingSMS(SessionContext context, Contact contact, String message);

	void onOutgoingCallAnswered(SessionContext context);

	void onOutgoingCallRejected(SessionContext context);

	void onOutgoingCallFailed(SessionContext context);

	void onOutgoingSMSDelivered(SessionContext context);

	void onOutgoingSMSRejected(SessionContext context);
}
