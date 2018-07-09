class global_counter{
  int[] classBuff;
  int besClas;
  int globCounter;
  
  global_counter(int buffVal){
    classBuff=new int[buffVal];
    besClas=0;
    globCounter=0;
  }
  
  int update_claVal(){
    int varCtr=0;
    int LenBuff=classBuff.length;
    int[] varSum=new int[LenBuff];
    int[] varArr=new int[LenBuff];
    //for(int i=0;i<LenBuff;i++) varSum[i]=0;
    int chkctr;
    int tempVal;
    for(int i=0;i<LenBuff;i++){
      tempVal=classBuff[i];
      chkctr=1;
      for(int j=0;j<varCtr;j++){
        if(tempVal==varArr[j]){
          varSum[j]++;
          chkctr=0;
        }
      }
      if(chkctr==1){
        varArr[varCtr]=tempVal;
        varSum[varCtr]=1;
        varCtr++;
      }
    }
    int maxSum=varSum[0];
    int maxInd=0;
    for(int i=0;i<varCtr;i++){
      if(varSum[i]>maxSum){
        maxSum=varSum[i];
        maxInd=i;
      }
    }
    return varArr[maxInd];
  }
      
  void update_buffer(int newCla){
    //println(newCla);
    int LenBuff=classBuff.length;
    for(int i=1;i<LenBuff;i++){
      classBuff[i-1]=classBuff[i];
    }
    classBuff[LenBuff-1]=newCla;
    besClas=update_claVal();
    globCounter++;
  }
}
