package net.kiborgov.argo.android.client.interfaces;

public interface PhoneClient {

	void answerCall();

	void rejectCall();

	void startCall(String number);

	void releaseCall();

	void sendSMS(String number, String message);
}
