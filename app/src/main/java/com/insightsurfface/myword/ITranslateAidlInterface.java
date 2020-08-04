/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.insightsurfface.myword;
public interface ITranslateAidlInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.insightsurfface.myword.ITranslateAidlInterface
{
private static final java.lang.String DESCRIPTOR = "com.insightsurfface.myword.ITranslateAidlInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.insightsurfface.myword.ITranslateAidlInterface interface,
 * generating a proxy if needed.
 */
public static com.insightsurfface.myword.ITranslateAidlInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.insightsurfface.myword.ITranslateAidlInterface))) {
return ((com.insightsurfface.myword.ITranslateAidlInterface)iin);
}
return new com.insightsurfface.myword.ITranslateAidlInterface.Stub.Proxy(obj);
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
case TRANSACTION_translate:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
com.insightsurfface.myword.aidl.ITranslateCallback _arg1;
_arg1 = com.insightsurfface.myword.aidl.ITranslateCallback.Stub.asInterface(data.readStrongBinder());
this.translate(_arg0, _arg1);
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements com.insightsurfface.myword.ITranslateAidlInterface
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
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
@Override
public void translate(java.lang.String word, com.insightsurfface.myword.aidl.ITranslateCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(word);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_translate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_translate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
/**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
public void translate(java.lang.String word, com.insightsurfface.myword.aidl.ITranslateCallback callback) throws android.os.RemoteException;
}
