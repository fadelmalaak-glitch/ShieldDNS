package com.fady.shielddns;

import android.app.*; import android.content.*; import android.net.VpnService; import android.os.Bundle; import android.widget.*;

public class MainActivity extends Activity {
 TextView status;
 @Override public void onCreate(Bundle b){ super.onCreate(b); setContentView(R.layout.activity_main); status=findViewById(R.id.status);
  findViewById(R.id.start).setOnClickListener(v->{ Intent p=VpnService.prepare(this); if(p!=null) startActivityForResult(p,7); else startFilter(); });
  findViewById(R.id.stop).setOnClickListener(v->{ startService(new Intent(this,FilterVpnService.class).setAction("STOP")); status.setText("Protection is OFF"); });
 }
 @Override protected void onActivityResult(int r,int c,Intent d){ super.onActivityResult(r,c,d); if(r==7 && c==RESULT_OK) startFilter(); }
 void startFilter(){ startForegroundService(new Intent(this,FilterVpnService.class).setAction("START")); status.setText("Protection is ON — Family Filter"); }
}
