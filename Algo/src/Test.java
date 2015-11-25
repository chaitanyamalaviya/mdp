//public class Test {
//    
//    public static void main(String[] args){
//        int[][] map=new int[][]{// map
//        		{0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,1,1,1,1,1,1,0,1,1,1,0},
//                {0,1,1,1,1,0,0,1,1,1,1,0},
//                {0,1,1,1,1,0,1,1,1,1,1,0},
//                {0,1,1,0,0,0,1,1,0,1,1,0},
//                {0,1,1,0,0,0,1,1,0,1,1,0},
//                {0,1,1,1,1,1,1,1,0,1,1,0},
//                {0,0,0,0,0,0,0,0,0,0,0,0}
//        };
//        AStar aStar=new AStar(map, 8, 12);
//        
//        int flag=aStar.searchResult();
//        
//        if(flag==-1){
//            System.out.println("import map fail");
//        }else if(flag==0){
//            System.out.println("no path");
//        }else{
//            for(int x=0;x<8;x++){
//                for(int y=0;y<12;y++){
//                    if(map[x][y]==1){
//                        System.out.print("1");
//                    }else if(map[x][y]==0){
//                        System.out.print("0");
//                    }else if(map[x][y]==-1){
//                        System.out.print("*");
//                    }
//                }
//                System.out.println();
//            }
//        }
//    }
//}