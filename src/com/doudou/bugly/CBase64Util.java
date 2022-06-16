package com.doudou.bugly;

import com.doudou.bugly.bean.BaseBean;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class CBase64Util {
	private static final String TAG= CBase64Util.class.getSimpleName();
		private static String characters="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_=";
		
		public static void main(String[] args) {
//			System.out.println(encode("172.18.3.216"));
//			System.out.println(decode("7bTl5rSstKS4oaO0urTx4LSstMHz9NHap7imvtnm8_jR2tPFpLim1f7k-fv_4_u_tLq08eS0rLTX8uTz-Pm-wtu_o6KmtLq0-eW0rLT3-PLk-f_ytLq0-eXg8-S0rLSuuKa4prS6tOC0rLSnuKS4r7S6tPr_tKy0p6GkuKSnuKKkuKevr7S6tOXhtKy0pa-ltLq05f60rLShrqO06w=="));
//			DatatypeConverter converter
//			System.out.println(decode(encode("172.18.9.89")));

			// 192.168.1.172
//			int ip = ((192 & 0xff) << 24) + ((168 & 0xff) << 16) + ((1 & 0xff) << 8 ) + (172 & 0xff);
//			System.out.println((ip >> 24) & 0xff);
//			System.out.println((ip >> 16) & 0xff);
//			System.out.println((ip >> 8) & 0xff);
//			System.out.println(ip & 0xff);

//			BaseBean target = new BaseBean();
//			ReferenceQueue queue = new ReferenceQueue();
//			WeakReference<Object> ref = new WeakReference(target, queue);
//			System.out.println("ref = " + ref);
//			System.gc();
//			System.out.println("before:" + queue.poll());
//			target = null;
//			System.gc();
//			System.out.println("after:" + queue.poll());
		}

		private static String encode(Integer[] ecodearr){
			int clen=ecodearr.length;
			StringBuilder builder=new StringBuilder();
			int[] arr = new int[4];
			for (int i = 0; i < clen;i++) {
				int cc=ecodearr[i];
				arr[0] = (cc >> 0x2);
				int nc=++i >= clen ? 0:ecodearr[i];
				arr[1] =  ((0x3 & cc) << 0x4 | nc >> 0x4);
				if (i >= clen) {
					arr[2] = arr[3] = 0x40;
				} else {
					int nnc = ++i >= clen ? 0:ecodearr[i];
					arr[2] = ((0xF & nc) << 0x2 | nnc >> 0x6);
					if (i >= clen) {
						arr[3] = 0x40;
					} else {
						arr[3] = (0x3F & nnc);
					}
				}
				for (int j : arr) {
					builder.append(characters.charAt(j));
//					System.out.print(j+"  ");
				}
			}
			return builder.toString();
		}
		
		
		public static String encode(String str){
			int strlen=str.length();
			char[] chars=str.toCharArray();
			List<Integer> list =new ArrayList<>();
			for (int i =0;i< strlen;i++) {
				if(chars[i] < 0x0 || chars[i] > 0x7F){
					if (0x800 > chars[i]) {
						list.add((0x3F & chars[i] | 0x80) ^ 0x96);
					} else {
						list.add((chars[i] >> 0xC | 0xE0) ^ 0x96);
						list.add((chars[i] >> 0x6 & 0x3F | 0x80) ^ 0x96);
						list.add((0x3F & chars[i] | 0x80) ^ 0x96);
					}
				}else{
					list.add(chars[i] ^ 0x96);
				}
			}
//			for (int a:list){
//				System.out.print(a+" ");
//				Log.e(TAG,""+a);
//			}
			Integer[] rets=list.toArray(new Integer[list.size()]);
			return new String(encode(rets));
		}
		
		
		public static String decode(String str){
			char[] chars=str.toCharArray();
			int[] cs=new int[4];
			List<Integer> list=new ArrayList<>();
			for (int i = 0; i < chars.length; ) {
				cs[0]=characters.indexOf(chars[i++]);
				cs[1]=characters.indexOf(chars[i++]);
				cs[2]=characters.indexOf(chars[i++]);
				cs[3]=characters.indexOf(chars[i++]);
				int chr1 = cs[0] << 0x2 | cs[1] >> 0x4 ;
				int chr2 = (0xF & cs[1]) << 0x4 | cs[2] >> 0x2;
				int chr3 = (0x3 & cs[2]) << 0x6 | cs[3];
				list.add(chr1 ^ 0x96);
				if(0x40 != cs[2]) list.add(chr2 ^ 0x96);
				if(0x40 != cs[3]) list.add(chr3 ^ 0x96);
			}
			return decode(list.toArray(new Integer[list.size()]));
		}
		
		private static String decode(Integer[] dcodearr){
			StringBuilder builder=new StringBuilder();
			for (int i = 0; i < dcodearr.length; ) {
				int v=dcodearr[i];
				if(v < 0x80){
					builder.append((char)v);
				}else if(v > 0xBF && v < 0xE0){
					builder.append((char)((0x1F & v) << 0x6 | 0x3F & dcodearr[++i]));
				}else{
	        		builder.append((char)((0xF & v) << 0xC | (0x3F & dcodearr[++i]) << 0x6 | 0x3F & dcodearr[++i]));
				}
				i++;
			}
			return builder.toString();
		} 
	}