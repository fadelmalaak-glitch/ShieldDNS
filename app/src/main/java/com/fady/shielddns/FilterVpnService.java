package com.fady.shielddns;

import android.app.*; import android.content.*; import android.net.VpnService; import android.os.*; import java.io.*; import java.net.*; import java.nio.*; import java.util.*;

public class FilterVpnService extends VpnService {
 private ParcelFileDescriptor tun; private volatile boolean running; private Thread worker;
 private static final String UPSTREAM="185.228.168.168"; // CleanBrowsing Family Filter
 @Override public int onStartCommand(Intent i,int flags,int id){ if(i!=null && "STOP".equals(i.getAction())){ stopNow(); return START_NOT_STICKY; } startNow(); return START_STICKY; }
 private void startNow(){ if(running)return; makeNotification(); try{
   tun=new Builder().setSession("ShieldDNS Family Filter").setMtu(1500).addAddress("10.77.0.1",32).addDnsServer("10.77.0.2").addRoute("10.77.0.2",32).establish();
   running=tun!=null; if(running){ worker=new Thread(this::loop,"ShieldDNS-DNS"); worker.start(); }
  }catch(Exception e){ stopNow(); }
 }
 private void makeNotification(){ String id="shield"; NotificationManager nm=getSystemService(NotificationManager.class); nm.createNotificationChannel(new NotificationChannel(id,"ShieldDNS protection",NotificationManager.IMPORTANCE_LOW)); Notification n=new Notification.Builder(this,id).setContentTitle("ShieldDNS active").setContentText("Strong family DNS filtering is running").setSmallIcon(android.R.drawable.ic_lock_lock).build(); startForeground(42,n); }
 private void loop(){ try(FileInputStream in=new FileInputStream(tun.getFileDescriptor()); FileOutputStream out=new FileOutputStream(tun.getFileDescriptor())){
   byte[] buf=new byte[32767]; while(running){ int n=in.read(buf); if(n<28)continue; byte[] packet=Arrays.copyOf(buf,n); byte[] resp=handleDns(packet); if(resp!=null) out.write(resp); }
  }catch(Exception ignored){}
 }
 private byte[] handleDns(byte[] p){ try{
   int ver=(p[0]>>4)&15; if(ver!=4)return null; int ihl=(p[0]&15)*4; if(ihl<20 || p.length<ihl+8 || (p[9]&255)!=17)return null;
   int dstPort=u16(p,ihl+2); if(dstPort!=53)return null; int udpLen=u16(p,ihl+4); int dnsLen=udpLen-8; if(dnsLen<=0 || ihl+8+dnsLen>p.length)return null;
   byte[] q=Arrays.copyOfRange(p,ihl+8,ihl+8+dnsLen); DatagramSocket s=new DatagramSocket(); protect(s); s.setSoTimeout(4000); DatagramPacket dp=new DatagramPacket(q,q.length,InetAddress.getByName(UPSTREAM),53); s.send(dp); byte[] rb=new byte[4096]; DatagramPacket rp=new DatagramPacket(rb,rb.length); s.receive(rp); s.close(); byte[] dns=Arrays.copyOf(rb,rp.getLength());
   int total=20+8+dns.length; byte[] r=new byte[total]; r[0]=0x45; r[1]=0; put16(r,2,total); put16(r,4,0); put16(r,6,0); r[8]=64; r[9]=17;
   System.arraycopy(p,16,r,12,4); System.arraycopy(p,12,r,16,4); put16(r,20,53); put16(r,22,u16(p,ihl)); put16(r,24,8+dns.length); put16(r,26,0); System.arraycopy(dns,0,r,28,dns.length); put16(r,10,checksum(r,0,20)); return r;
  }catch(Exception e){ return null; }
 }
 private static int u16(byte[] b,int o){ return ((b[o]&255)<<8)|(b[o+1]&255); }
 private static void put16(byte[] b,int o,int v){ b[o]=(byte)(v>>8); b[o+1]=(byte)v; }
 private static int checksum(byte[] b,int o,int len){ long sum=0; for(int i=0;i<len;i+=2){ int hi=b[o+i]&255, lo=(i+1<len)?b[o+i+1]&255:0; sum+=(hi<<8)|lo; while((sum>>16)!=0)sum=(sum&0xffff)+(sum>>16); } return (int)(~sum)&0xffff; }
 private void stopNow(){ running=false; try{if(tun!=null)tun.close();}catch(Exception ignored){} tun=null; stopForeground(STOP_FOREGROUND_REMOVE); stopSelf(); }
 @Override public void onDestroy(){ stopNow(); super.onDestroy(); }
}
