package com.example.lusog.compartircuentas;



public  class CodificacionCuentas {

    public static String getStringCodificado(long numero){
        String[] arrayLetras={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","1","2","3","4","5","6"};
        int[] arrayNumeros={    0, 1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9 , 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};


        String stringNumero=Long.toString(numero);
        String stringLetras="";

        String letrasAnalizadas;

        //año
        letrasAnalizadas=stringNumero.substring(0,2);//siempre será 20, pero bueno
        stringLetras+=arrayLetras[Integer.parseInt(letrasAnalizadas)];//lo añadimos al string
        stringNumero=stringNumero.substring(2);//quito el primero
        letrasAnalizadas=stringNumero.substring(0,2);//a partir de 2031 fallará
        stringLetras+=arrayLetras[Integer.parseInt(letrasAnalizadas)];
        stringNumero=stringNumero.substring(2);
        //mes
        letrasAnalizadas=stringNumero.substring(0,2);
        stringLetras+=arrayLetras[Integer.parseInt(letrasAnalizadas)];
        stringNumero=stringNumero.substring(2);
        //dia
        letrasAnalizadas=stringNumero.substring(0,2);
        stringLetras+=arrayLetras[Integer.parseInt(letrasAnalizadas)];
        stringNumero=stringNumero.substring(2);
        //hora
        letrasAnalizadas=stringNumero.substring(0,2);
        stringLetras+=arrayLetras[Integer.parseInt(letrasAnalizadas)];
        stringNumero=stringNumero.substring(2);
        //minuto - aquí voy de uno en uno porque puede ser hasta 60
        letrasAnalizadas=stringNumero.substring(0,1);
        stringLetras+=arrayLetras[Integer.parseInt(letrasAnalizadas)];
        stringNumero=stringNumero.substring(1);
        letrasAnalizadas=stringNumero.substring(0,1);
        stringLetras+=arrayLetras[Integer.parseInt(letrasAnalizadas)];




        return stringLetras;//temporal
    }

    public static long getLongDesCodificado(String texto){

        char[] arrayLetras={'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','1','2','3','4','5','6'};

        String stringNumeros="";

        int i,numeroCaracter;
        boolean encontradoEquivalencia;

        numeroCaracter=0;

        for(char c:texto.toCharArray()){
            numeroCaracter++;
            i=0;
            encontradoEquivalencia=false;

            while(!encontradoEquivalencia && i<arrayLetras.length){
                if(Character.toLowerCase(c)==Character.toLowerCase(arrayLetras[i])){
                    encontradoEquivalencia=true;
                    if(numeroCaracter<6 && i<10){
                        stringNumeros+=0;
                    }
                    stringNumeros+=i;
                }

                i++;
            }
        }



        return Long.parseLong(stringNumeros);
    }

}
