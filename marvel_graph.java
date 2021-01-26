import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Iterator;
import java.util.Map;

 class graph{
 	private HashMap<String,Integer> node_to_id;
 	private HashMap<Integer,String> id_to_node;
 	private HashMap<Integer,Vector<Integer>> adj;
 	private HashMap<Integer,Vector<Integer>> stories;
 	private int[] adj_count;
 	private long[] co_occurrence;
 	private int[] done;
 	private int V;
 	private int components;
 	
 	



 	public graph(){
 		this.V=0;
 		this.components=0;
 		node_to_id=new HashMap<String,Integer>();
 		id_to_node=new HashMap<Integer,String>();

 	}


 	private String[] split(String s){
 		int c=0;
 		int k=0;
 		int j=0;
 		String ss[]= new String[3];
 		for(int i=0;i<s.length();i++){
 			if(s.charAt(i)=='"')c++;
 			else if(s.charAt(i)==',' && c%2==0){
 				ss[k]=s.substring(j,i);
 				j=i+1;
 				k++;
 			}
 		}
 		if(j!=s.length())ss[k]=s.substring(j,s.length());
 		return ss;
 	}


 	public void addNodes(String nodefile){
 		try {
	 		File file_node= new File(nodefile);
	 		FileReader read_node = new FileReader(file_node);
	      	BufferedReader read = new BufferedReader(read_node);
	      	String next_line="";
	      	String[] split_terms;
	      	int flag=0;
	      	while ((next_line = read.readLine()) != null) {
	      		if(flag==0){flag=1;continue;}
	        	split_terms = split(next_line);    
	        	String node=split_terms[1];
	        	if(node.charAt(0)=='"'){
	        		node_to_id.put(node.substring(1,node.length()-1),V);
	        		id_to_node.put(V,node.substring(1,node.length()-1));
	        	}
	        	else{
	        		node_to_id.put(node,V);	
	        		id_to_node.put(V,node);
	        	}
	        	V++;
	        }
	        read.close();
	        adj_count= new int[V];
	        co_occurrence = new long[V];
	        adj = new HashMap<Integer,Vector<Integer>>();
	        for(int i=0; i<V; i++){
	        	Vector<Integer> temp= new Vector<Integer>();
	        	adj.put(i,temp);
	        	adj_count[i]=0;                          ///////////////////////////
	        	co_occurrence[i]=0;                      ///////////////////////////
	        }
        }
    	catch(IOException ioe) {
      		ioe.printStackTrace();
    	}
 	}

 	public void addEdges(String edgeFile){
 		try{
	 		File file_edge= new File(edgeFile);
	 		FileReader read_edge = new FileReader(file_edge);
	      	BufferedReader read = new BufferedReader(read_edge);
	      	String next_line="";
	      	String[] split_terms;
	      	int flag=0;
	      	while ((next_line = read.readLine()) != null) {
	      		if(flag==0){flag=1;continue;}
	        	split_terms = split(next_line);        
	        	String node1,node2;
	        	if(split_terms[0].charAt(0)=='"'){
	        		node1=split_terms[0].substring(1,split_terms[0].length()-1);
	        	}
	        	else node1 =split_terms[0];
	        	if(split_terms[1].charAt(0)=='"'){
	        		node2=split_terms[1].substring(1,split_terms[1].length()-1);
	        	}
	        	else node2 =split_terms[1];
	        	if(node_to_id.containsKey(node1) && node_to_id.containsKey(node2)){
		        	int n1=node_to_id.get(node1);
		        	int n2=node_to_id.get(node2);
		        	long weigh=Long.parseLong(split_terms[2]);
		        	adj.get(n1).add(n2);
		        	adj.get(n2).add(n1);
		        	adj_count[n1]++;
		        	adj_count[n2]++;
		        	co_occurrence[n1]+=weigh;
		        	co_occurrence[n2]+=weigh;
		        }
	        	else{
	        		System.out.println("wrong input");               ////////////////////////////////
	        	}
	 		}
	 		read.close();
	 	}
	 	catch(IOException ioe){
	 		ioe.printStackTrace();
	 	}
 	}


 	public void average(){
 		double temp=0;
 		for(int i=0;i<V;i++){
 			temp+=(double)adj_count[i];
 		}
 		temp/=V;
 		String s=String.format("%.2f",temp);
 		System.out.println(s);
		
 	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


 	private int str_cmp(String s1, String s2){
 		int l1=s1.length();
 		int l2=s2.length();
 		int i=0;
 		while(i<l1 && i<l2){
 			if(s1.charAt(i)>s2.charAt(i))return 1;
 			else if(s1.charAt(i)==s2.charAt(i)){i++;continue;}
 			else return -1;
 		}
 		if(i<s1.length())return 1;
 		else return -1;
 	}



 	private int compare(int i,int j){
 		if(co_occurrence[i]>co_occurrence[j])return 1;
 		else if(co_occurrence[i]==co_occurrence[j]){
 			String n1=id_to_node.get(i);
 			String n2=id_to_node.get(j);
 			int flag=str_cmp(n1,n2);
 			if(flag>0)return 1;
 			else return -1;
 		}
 		else return -1;
 	}


 	private int[] merge(int[] a1, int[] a2){
 		int l=a1.length+a2.length;
 		int[] merged= new int[l];
 		int i=0;
 		int j=0;
 		int k=0;
 		while(i<a1.length && j<a2.length){
 			if(compare(a1[i],a2[j])>0){                       //a1[i]>a2[j]
 				merged[k]=a1[i];
 				i++;
 				k++;
 			}
 			else{
 				merged[k]=a2[j];
 				j++;
 				k++;
 			}
 		}
 		if(i<a1.length){
 			while(i<a1.length){
 				merged[k]=a1[i];
 				i++;
 				k++;
 			}
 		}
 		if(j<a2.length){
 			while(j<a2.length){
 				merged[k]=a2[j];
 				j++;
 				k++;
 			}
 		}
 		return merged;
 	}

 	private int[] mergesort(int left, int right){
 		if(left==right){
 			int[] a=new int[1];
 			a[0]=left;
 			return a;
 		}
 		int mid=(left+right)/2;
 		int[] l1=mergesort(left,mid);
 		int[] l2=mergesort(mid+1,right);
 		int[] c=merge(l1,l2);
 		return c;
 	}


 	public void rank(){
 		int[] sorted=mergesort(0,V-1);
 		for(int i=0;i<V;i++){
 			if(i<V-1)System.out.print(id_to_node.get(sorted[i])+",");
 			else System.out.print(id_to_node.get(sorted[i]));
 		}
 		System.out.println();
 	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

 	public void storylines(){
 		stories = new HashMap<Integer,Vector<Integer>>();
 		done = new int[V];
 		for(int i=0;i<V;i++){
 			done[i]=0;
 		}
 		for(int i=0;i<V;i++){
 			if(done[i]==0){
 				Vector<Integer> st_line = new Vector<Integer>();
 				dfs(i,st_line);
 				stories.put(components,st_line);
 				components++;
 			}
 		}
 		for(int i=0;i<components;i++){
 			lexi_sort(i);
 		}
 		int[] sorted_story = story_sort(0,components-1);
 		for(int i=0;i<components;i++){
 			story_print(sorted_story[i]);
 		}
 	}

 	private void story_print(int i){
 		Vector<Integer> story_i=stories.get(i);
 		for(int j=0;j<story_i.size();j++){
 			if(j==story_i.size()-1)System.out.print(id_to_node.get(story_i.get(j)));
 			else System.out.print(id_to_node.get(story_i.get(j))+",");
 		}
 		System.out.println();
 	}

 	private int[] story_sort(int left,int right){
 		if(left==right){
 			int[] a=new int[1];
 			a[0]=left;
 			return a;
 		}
 		int mid=(left+right)/2;
 		int[] a1 = story_sort(left,mid);
 		int[] a2 = story_sort(mid+1,right);
 		int[] a3 = merge_story(a1,a2);
 		return a3;
 	}

 	private int[] merge_story(int[] l1, int[] l2){
 		int l=l1.length+l2.length;
 		int i=0;
 		int j=0;
 		int k=0;
 		int[] st_sort= new int[l];
 		while(i<l1.length && j<l2.length){
 			if(story_cmp(l1[i],l2[j])>0){
 				st_sort[k]=l1[i];
 				i++;
 				k++;
 			}
 			else{
 				st_sort[k]=l2[j];
 				j++;
 				k++;
 			}
 		}
 		if(i<l1.length){
 			while(i<l1.length){
 				st_sort[k]=l1[i];
 				i++;
 				k++;
 			}
 		}
 		if(j<l2.length){
 			while(j<l2.length){
 				st_sort[k]=l2[j];
 				j++;
 				k++;
 			}
 		}
 		return st_sort;
 	}


 	private int story_cmp(int i,int j){
 		Vector<Integer> story_i=stories.get(i);
 		Vector<Integer> story_j=stories.get(j);
 		if(story_i.size()>story_j.size())return 1;
 		else if(story_i.size()==story_j.size()){
 			String s1=id_to_node.get(story_i.get(0));
 			String s2=id_to_node.get(story_j.get(0));
 			if(str_cmp(s1,s2)>0)return 1;
 			else return -1;
 		}
 		else return -1;
 	}


 	private void dfs(int node, Vector<Integer> s_line){
 		done[node]=1;
 		s_line.add(node);
 		for(int j=0;j<adj.get(node).size();j++){
 			if(done[adj.get(node).get(j)]==0){
 				dfs(adj.get(node).get(j),s_line);
 			}
 		}
 	}

 	private void lexi_sort(int comp_no){
 		Vector<Integer> a = sort_lexic(0,stories.get(comp_no).size()-1,comp_no);
 		stories.put(comp_no,a);
 	}

 	private Vector<Integer> sort_lexic(int left, int right, int comp){
 		if(left==right){
 			Vector<Integer> a = new Vector<Integer>();
 			a.add(stories.get(comp).get(left));
 			return a;
 		}
 		int mid=(left+right)/2;
 		Vector<Integer> a1=sort_lexic(left,mid,comp);
 		Vector<Integer> a2=sort_lexic(mid+1,right,comp);
 		Vector<Integer> a3=lexi_merge(a1,a2);
 		return a3;
 	}


 	private Vector<Integer> lexi_merge(Vector<Integer> l1, Vector<Integer> l2){
 		int l=l1.size()+l2.size();
 		Vector<Integer> lexi_sorted= new Vector<Integer>();
 		int i=0;
 		int j=0;
 		while(i<l1.size() && j<l2.size()){
 			String s1=id_to_node.get(l1.get(i));
 			String s2=id_to_node.get(l2.get(j));
 			if(str_cmp(s1,s2)>0){
 				lexi_sorted.add(l1.get(i));
 				i++;
 			}
 			else{
 				lexi_sorted.add(l2.get(j));
 				j++;
 			}
 		}
 		if(i<l1.size()){
 			while(i<l1.size()){
	 			lexi_sorted.add(l1.get(i));
	 			i++;
	 		}
 		}
 		if(j<l2.size()){
 			while(j<l2.size()){
	 			lexi_sorted.add(l2.get(j));
	 			j++;
	 		}
 		}
 		return lexi_sorted;
 	}
}




public class marvel_graph{
	public static void main(String args[]){
		String node_file = args[0];
		String edge_file = args[1];
		String function = args[2];
		graph gr= new graph();
		gr.addNodes(node_file);
		gr.addEdges(edge_file);
		if(function.equals("average")){
			gr.average();
		}
		if(function.equals("rank")){
			gr.rank();
		}
		if(function.equals("independent_storylines_dfs")){
			gr.storylines();
		}
	}
}