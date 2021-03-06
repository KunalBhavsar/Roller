package com.rollingscenes.src;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.rollingscenes.R;
import com.rollingscenes.src.utils.KeyConstants;

public class CrashDisplayActivity extends Activity {
    
    private CrashDisplayActivity mActivityContecxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_crash);
        
        mActivityContecxt = CrashDisplayActivity.this;
        
        Throwable exception = (Throwable) getIntent().getSerializableExtra("ErrorContent");

        final StringBuilder report = new StringBuilder();
        Date curDate = new Date();
        report.append("Error Report collected on : ")
                .append(curDate.toString()).append('\n').append('\n');
        report.append("Informations :").append('\n');
        addInformation(report);
        report.append('\n').append('\n');
        report.append("Stack:\n");
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        exception.printStackTrace(printWriter);
        report.append(result.toString());
        printWriter.close();
        report.append('\n');
        report.append("Error :\n");
        report.append(exception.getMessage());
        report.append('\n');
        report.append("**** End of current Report ***");

        ((Button)findViewById(R.id.btnClose)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        finish();
		        System.exit(0);
			}
		});
        ((Button)findViewById(R.id.btnReport)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                String subject = "RollingScenes App crashed!";
                
                StringBuilder body = new StringBuilder();
                body.append('\n').append('\n');
                body.append(report).append('\n').append('\n');
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("message/rfc822");
                sendIntent.putExtra(Intent.EXTRA_EMAIL,new String[] { KeyConstants.EMAIL_MAIL_TO });
                sendIntent.putExtra(Intent.EXTRA_TEXT,body.toString());
                sendIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
                sendIntent.setType("message/rfc822");
                mActivityContecxt.startActivity(sendIntent);
                
                finish();
                System.exit(0);
			}
		});
	}
	
    private void addInformation(StringBuilder message) {
        message.append("Locale: ").append(Locale.getDefault()).append('\n');
        try {
            PackageManager pm = mActivityContecxt.getPackageManager();
            PackageInfo pi;
            pi = pm.getPackageInfo(mActivityContecxt.getPackageName(), 0);
            message.append("Version: ").append(pi.versionName).append('\n');
            message.append("Package: ").append(pi.packageName).append('\n');
        } 
        catch (Exception e) {
            Log.e("CustomExceptionHandler", "Error", e);
            message.append("Could not get Version information for ").append(
            		mActivityContecxt.getPackageName());
        }
        message.append("Phone Model: ").append(android.os.Build.MODEL)
                .append('\n');
        message.append("Android Version: ")
                .append(android.os.Build.VERSION.RELEASE).append('\n');
        message.append("Board: ").append(android.os.Build.BOARD).append('\n');
        message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
        message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
        message.append("Host: ").append(android.os.Build.HOST).append('\n');
        message.append("ID: ").append(android.os.Build.ID).append('\n');
        message.append("Model: ").append(android.os.Build.MODEL).append('\n');
        message.append("Product: ").append(android.os.Build.PRODUCT)
                .append('\n');
        message.append("Type: ").append(android.os.Build.TYPE).append('\n');
        StatFs stat = getStatFs();
        message.append("Total Internal memory: ")
                .append(getTotalInternalMemorySize(stat)).append('\n');
        message.append("Available Internal memory: ")
                .append(getAvailableInternalMemorySize(stat)).append('\n');
    }
 
    private StatFs getStatFs() {
        File path = Environment.getDataDirectory();
        return new StatFs(path.getPath());
    }
 
    private long getAvailableInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }
 
    private long getTotalInternalMemorySize(StatFs stat) {
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }
}
