package org.testapp.aty;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.testapp.R;
import org.testapp.binder_pool.BinderPool;
import org.testapp.impl.ComputeImpl;
import org.testapp.impl.SecurityCenterImpl;

public class BinderPoolActivity extends AppCompatActivity {
	private SecurityCenterImpl mSecurityCenter;
	private ComputeImpl mCompute;
	private static final String TAG = "BinderPoolActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_binder_pool);
		new Thread(new Runnable() {
			@Override
			public void run() {
				doWork();

			}
		}).start();
	}

	private void doWork() {
		//注意意外死亡的情况，需要重新获取
		BinderPool binderPool = BinderPool.getInstance(BinderPoolActivity.this);
		IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
		mSecurityCenter = (SecurityCenterImpl) SecurityCenterImpl.asInterface(securityBinder);
		Log.d(TAG, "visit ISecurityCenter");
		String msg = "helloworld-安卓";
		System.out.println("content : " + msg);
		try {
			String password = mSecurityCenter.encrypt(msg);
			System.out.println("encrypt : " + password);
			System.out.println("decrypt : " + mSecurityCenter.decrypt(password));
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		Log.d(TAG, "visit ICompute");
		IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
		mCompute = (ComputeImpl) ComputeImpl.asInterface(computeBinder);
		try {
			System.out.println("3 + 5 = " + mCompute.add(3, 5));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
