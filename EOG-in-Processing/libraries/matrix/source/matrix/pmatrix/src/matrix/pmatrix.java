package matrix;

public class pmatrix {
	
	/* -->> add function is for testing operations1*/
	
	public static float identity[][];
	      
	    /* function for calculating local stiffness Matrix*/
	    
	    public static float[][] identityMatrix(int matrixSize)
	    {
	    	
	/* -->>    arr[1][1] = a+b;  arr hasn't been initialised, 
	 * you're accessing [1][1] when the array is null. Also, 
	 * you probably want 0, not 1, since arrays are 0-index */
	    	
	 if(identity==null)
	 {
	   identity=new float[matrixSize][matrixSize];
	 }

  	 for ( int a=0; a<matrixSize; a++ ){
  		 for ( int b=0; b<matrixSize; b++ ){
  		 
  			 if (a==b){
  				 identity[a][b] = (float) (1.0);
  			}
  			 if (a!=b){
  				 identity[a][b] = (float) (0.0);
  			}
  		 }
  	 }
	 /* return itentity matrix value
	  * Function to be accessed by user from processing */
	 
	return identity;
	    }


 
		/* -->> add function is for testing operations1*/
		
		public static float transpose[][];
		      
		    /* function for calculating local stiffness Matrix*/
		    
		    public static float[][] transposeMatrix(int matrixSizerow,int matrixSizecolumn, float matrixA[][])
		    {
		    	
		    	
		 if(transpose==null)
		 {
			 transpose=new float[matrixSizerow][matrixSizecolumn];
		 }

		 //set answere matrix to zero	
	  	 for ( int a=0; a<matrixSizerow; a++ ){
	  		 for ( int b=0; b<matrixSizecolumn; b++ ){
	  	
	  			transpose[a][b] = (float) (0.0);
	  			
	  		 }
	  	 }
	  	 
	  	 //solve for multiplication matrix
	  	 for ( int c=0; c<matrixSizerow; c++ ){
	  		 for ( int d=0; d<matrixSizecolumn; d++ ){
	  			 	

	  				
	  			transpose[c][d] += (float) (matrixA[d][c]);
	  				
	  			
	  		 }
	  	 }	 
	  	 
		 /* return multiplication matrix value
		  * Function to be accessed by user from processing */
		 
		return transpose;
		    }	    
	    
	    
	    

		/* -->> add function is for testing operations1*/
		
		public static float multiply[][];
		      
		    /* function for calculating local stiffness Matrix*/
		    
		    public static float[][] multiplySquareMatrix(int matrixSizerow,int matrixSizecolumn, float matrixA[][], float matrixB[][])
		    {
		    	
		    	
		 if(multiply==null)
		 {
			 multiply=new float[matrixSizerow][matrixSizecolumn];
		 }

		 //set answere matrix to zero	
	  	 for ( int a=0; a<matrixSizerow; a++ ){
	  		 for ( int b=0; b<matrixSizecolumn; b++ ){
	  	
	  				multiply[a][b] = (float) (0.0);
	  			
	  		 }
	  	 }
	  	 
	  	 //solve for multiplication matrix
	  	 for ( int c=0; c<matrixSizerow; c++ ){
	  		 for ( int d=0; d<matrixSizecolumn; d++ ){
	  			 	
	  			for ( int i=0; i<matrixSizecolumn; i++ ){
	  				
	  				multiply[c][d] += (float) (matrixA[c][i] * matrixB[i][d]);
	  				
	  			}
	  		 }
	  	 }	 
	  	 
		 /* return multiplication matrix value
		  * Function to be accessed by user from processing */
		 
		return multiply;
		    }

		    
		    
		    
			/* -->> add function is for testing operations1*/
			
			public static float add[][];
			      
			    /* function for calculating local stiffness Matrix*/
			    
			    public static float[][] addSquareMatrix(int matrixSizerow,int matrixSizecolumn, float matrixA[][], float matrixB[][])
			    {
			    	
			    	
			 if(add==null)
			 {
				 add=new float[matrixSizerow][matrixSizecolumn];
			 }

			 //set answere matrix to zero	
		  	 for ( int a=0; a<matrixSizerow; a++ ){
		  		 for ( int b=0; b<matrixSizecolumn; b++ ){
		  	
		  			add[a][b] = (float) (0.0);
		  			
		  		 }
		  	 }
		  	 
		  	 //solve for multiplication matrix
		  	 for ( int c=0; c<matrixSizerow; c++ ){
		  		 for ( int d=0; d<matrixSizecolumn; d++ ){
		  			 	
	
		  				
		  				add[c][d] += (float) (matrixA[c][d] + matrixB[c][d]);
		  				
		  			
		  		 }
		  	 }	 
		  	 
			 /* return multiplication matrix value
			  * Function to be accessed by user from processing */
			 
			return add;
			    }
	    
			    
			    
			    
				/* -->> add function is for testing operations1*/
				
				public static float subtract[][];
				      
				    /* function for calculating local stiffness Matrix*/
				    
				    public static float[][] subtractSquareMatrix(int matrixSizerow,int matrixSizecolumn, float matrixA[][], float matrixB[][])
				    {
				    	
				    	
				 if(subtract==null)
				 {
					 subtract=new float[matrixSizerow][matrixSizecolumn];
				 }

				 //set answere matrix to zero	
			  	 for ( int a=0; a<matrixSizerow; a++ ){
			  		 for ( int b=0; b<matrixSizecolumn; b++ ){
			  	
			  			subtract[a][b] = (float) (0.0);
			  			
			  		 }
			  	 }
			  	 
			  	 //solve for multiplication matrix
			  	 for ( int c=0; c<matrixSizerow; c++ ){
			  		 for ( int d=0; d<matrixSizecolumn; d++ ){			  			 			
			  				
			  			subtract[c][d] += (float) (matrixA[c][d] - matrixB[c][d]);
			  							  			
			  		 }
			  	 }	 
			  	 
				 /* return multiplication matrix value
				  * Function to be accessed by user from processing */
				 
				return subtract;
				    }			    
			    
			    
	    
				    
				    
				    
					/* -->> add function is for testing operations1*/
					
					public static float scale[][];
					      
					    /* function for calculating local stiffness Matrix*/
					    
					    public static float[][] scaleSquareMatrix(int matrixSizerow,int matrixSizecolumn, float matrixA[][], float scaleFactor)
					    {
					    	
					    	
					 if(scale==null)
					 {
						 scale=new float[matrixSizerow][matrixSizecolumn];
					 }

					 //set answere matrix to zero	
				  	 for ( int a=0; a<matrixSizerow; a++ ){
				  		 for ( int b=0; b<matrixSizecolumn; b++ ){
				  	
				  			scale[a][b] = (float) (0.0);
				  			
				  		 }
				  	 }
				  	 
				  	 //solve for multiplication matrix
				  	 for ( int c=0; c<matrixSizerow; c++ ){
				  		 for ( int d=0; d<matrixSizecolumn; d++ ){			  			 			
				  				
				  			scale[c][d] += (float) (matrixA[c][d] * scaleFactor);
				  							  			
				  		 }
				  	 }	 
				  	 
					 /* return multiplication matrix value
					  * Function to be accessed by user from processing */
					 
					return scale;
					    }				    

					    
					    
					    
						/* -->> add function is for testing operations1*/
						
						public static float columnmultiply[];
						      
						    /* function for calculating local stiffness Matrix*/
						    
						    public static float[] columnMultiplyMatrix(int matrixSizerow,int matrixSizecolumn, float matrixA[], float matrixB[][])
						    {
						    	
						    	
						 if(columnmultiply==null)
						 {
							 columnmultiply=new float[matrixSizecolumn];
						 }

						 //set answere matrix to zero	
					  	 for ( int a=0; a<matrixSizerow; a++ ){
					  		 for ( int b=0; b<matrixSizecolumn; b++ ){
					  	
					  			columnmultiply[a] = (float) (0.0);
					  			
					  		 }
					  	 }
					  	 
					  	 //solve for multiplication matrix
					  	 for ( int c=0; c<matrixSizerow; c++ ){
					  		 for ( int d=0; d<matrixSizecolumn; d++ ){
						  								  				
						  				columnmultiply[c] += (float) (matrixA[d] * matrixB[c][d]);
						  			
					  		 }
					  	 }	 
					  	 
						 /* return multiplication matrix value
						  * Function to be accessed by user from processing */
						 
						return columnmultiply;
						    }
						    


				    
}