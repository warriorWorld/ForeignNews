/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.insightsurfface.myword.aidl;
public interface ITranslateCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.insightsurfface.myword.aidl.ITranslateCallback
{
private static final java.lang.String DESCRIPTOR = "com.insightsurfface.myword.aidl.ITranslateCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.insightsurfface.myword.aidl.ITranslateCallback interface,
 * generating a proxy if needed.
 */
public static com.insightsurfface.myword.aidl.ITranslateCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.insightsurfface.myword.aidl.ITranslateCallback))) {
return ((com.insightsurfface.myword.aidl.ITranslateCallback)iin);
}
return new com.insightsurfface.myword.aidl.ITranslateCallback.Stub.Proxy(obj);
}
@Override
public android.os.IBinder asBinder()
{
return this;
}
@Override
public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
java.lang.String descriptor = DESCRIPTOR;
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(descriptor);
return true;
}
case TRANSACTION_onResponse:
{
data.enforceInterface(descriptor);
com.insightsurfface.myword.aidl.TranslateWraper _arg0;
if ((0!=data.readInt())) {
_arg0 = com.insightsurfface.myword.aidl.TranslateWraper.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.onResponse(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onFailure:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
this.onFailure(_arg0);
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements com.insightsurfface.myword.aidl.ITranslateCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override
public void onResponse(com.insightsurfface.myword.aidl.TranslateWraper translate) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((translate!=null)) {
_data.writeInt(1);
translate.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_onResponse, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override
public void onFailure(java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_onFailure, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onResponse = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onFailure = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void onResponse(com.insightsurfface.myword.aidl.TranslateWraper translate) throws android.os.RemoteException;
public void onFailure(java.lang.String message) throws android.os.RemoteException;
}
