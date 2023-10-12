package jmorph.hangle;

import geometry.MyPoint2D;
import geometry.MyPolygon;
import jmorph.maths.Complex;
import jmorph.maths.ComplexVector;

/** Translation of Fortran code hcurve by James Crampton.
 * @author Peter Lelievre
 */
public class HCurve {

    /** The only method in the class.
     * @param coeffs Array of coefficients from the HAngle outline analysis.
     * @param ncoeff The number of coefficients.
     * @return
     */
    public static MyPolygon run( ComplexVector coeffs, int ncoeff ) {

      final int NFFT=(int)Math.pow(2,10), NFFTP=NFFT+1, NFFT2=NFFT/2, NFFT2P=NFFT2+1;

      double[] X = new double[NFFT2];
      double[] Y = new double[NFFT2];
      double[] AEQV = new double[2*NFFT];
      Complex[] CA = new Complex[NFFT];
      double[] A = new double[NFFTP];
      double[] CADJ = new double[NFFTP];
      double[] SADJ = new double[NFFTP];
      double PI,PI2,CONV,DA,ARG,SINC,DSINC,DC,DS,DDZ,CVAL,SVAL;
      Complex[] Z = new Complex[NFFTP];
      Complex IMAG,DZDC,DZDS,EXPA,IEXPA,ZADJ;
      // EQUIVALENCE (CA,AEQV)

      Complex com, com1, com2; // working variables
      double wk1, wk2; // working variables

      PI = 4.0*Math.atan(1.0);
      PI2 = 2.0*PI;
      CONV = 1.0/PI;
      IMAG = new Complex(0.0,1.0);

      int IP = 3;
      int I = 1;
      while (true) {
          if (I>coeffs.size()) { break; }
          if ( ncoeff>=2 && IP>ncoeff ) { break; }
          X[IP-1] = coeffs.get(I-1).real();
          Y[IP-1] = coeffs.get(I-1).imag();
          IP += 1;
          I += 1;
      }
      int IMAX = IP - 1;

      CA[0] = new Complex(0.0,0.0);
      CA[1] = new Complex(0.0,0.0);
      CA[NFFT-1] = new Complex(0.0,0.0);
      int IM = NFFT - 1;
      for ( I=3 ; I<=IMAX ; I++ ) {
        CA[I-1] = new Complex(X[I-1],Y[I-1]);
        CA[IM-1] = CA[I-1].conjugate();
        IM -= 1;
      }
      for ( I=IP ; I<=NFFT2 ; I++ ) {
        CA[I-1] = new Complex(0.0,0.0);
        CA[IM-1] = new Complex(0.0,0.0);
        IM -= 1;
      }
      CA[NFFT2P-1] = new Complex(0.0,0.0);

      // Have to deal with CA,AEQV equivalence:
      for ( int k=1 ; k<=NFFT ; k++ ) {
          AEQV[2*(k-1)  ] = CA[k-1].real();
          AEQV[2*(k-1)+1] = CA[k-1].imag();
      }

      // SUBROUTINE FOUR1

      double WR,WI,WPR,WPI,WTEMP,THETA;
      int N = 2*NFFT;
      int J = 1;
      for ( I=1 ; I<=N ; I+=2 ) {
        if (J>I) {
          double TEMPR = AEQV[J-1];
          double TEMPI = AEQV[J];
          AEQV[J-1] = AEQV[I-1];
          AEQV[J] = AEQV[I];
          AEQV[I-1] = TEMPR;
          AEQV[I] = TEMPI;
        }
        int M = N/2;
        while ( M>=2 && J>M ) {
          J -= M;
          M /= 2;
        }
        J += M;
      }
      int MMAX = 2;
      while ( N>MMAX ) {
        int ISTEP = 2*MMAX;
        THETA = 6.28318530717959 / MMAX;
        WPR = -2.0*Math.pow( Math.sin(0.5*THETA) , 2 );
        WPI = Math.sin(THETA);
        WR = 1.0;
        WI = 0.0;
        for ( int M=1 ; M<=MMAX ; M+=2 ) {
          for ( I=M ; I<=N ; I+=ISTEP ) {
            J = I + MMAX;
            double TEMPR = WR*AEQV[J-1] - WI*AEQV[J];
            double TEMPI = WR*AEQV[J] + WI*AEQV[J-1];
            AEQV[J-1] = AEQV[I-1] - TEMPR;
            AEQV[J] = AEQV[I] - TEMPI;
            AEQV[I-1] += TEMPR;
            AEQV[I] += TEMPI;
          }
          WTEMP = WR;
          WR = WR*WPR - WI*WPI + WR;
          WI = WI*WPR + WTEMP*WPI + WI;
        }
        MMAX = ISTEP;
      }

      // END SUBROUTINE FOUR1

      // Have to deal with CA,AEQV equivalence:
      for ( int k=1 ; k<=NFFT ; k++ ) {
          double re = AEQV[2*(k-1)  ];
          double im = AEQV[2*(k-1)+1];
          CA[k-1] = new Complex(re,im);
      }

      DA = PI2 / NFFT;
      for ( I=1 ; I<=NFFT ; I++ ) {
        ARG = DA*(I-1);
        A[I-1] = CA[I-1].real() + ARG;
        CADJ[I-1] = Math.cos(ARG);
        SADJ[I-1] = Math.sin(ARG);
      }
      A[NFFTP-1] = A[0] + PI2;
      CADJ[NFFTP-1] = CADJ[0];
      SADJ[NFFTP-1] = SADJ[0];
      CVAL = 0.0;
      SVAL = 0.0;
      int ITER = 0;

      while (true) {
        
        Z[0] = new Complex(0.0,0.0);
        DZDC = new Complex(0.0,0.0);
        DZDS = new Complex(0.0,0.0);
        IM = 1;
        for ( I=2 ; I<=NFFTP ; I++ ) {
          ARG = 0.5*(A[IM-1]+A[I-1]);
          EXPA = new Complex( Math.cos(ARG) , Math.sin(ARG) );
          IEXPA = IMAG.times(EXPA);
          if ( A[I-1] == A[IM-1] ) {
            SINC = 1.0;
            DSINC = 0.0;
          } else {
            ARG = 0.5*(A[I-1]-A[IM-1]);
            SINC = Math.sin(ARG)/ARG;
            DSINC = (Math.cos(ARG)-SINC)/ARG;
          }
          com = EXPA.times(DA*SINC);
          Z[I-1] = Z[IM-1].plus(com);

      // Complex DZDC,DZDS,EXPA,IEXPA;
          wk1 = ( CADJ[I-1] - CADJ[IM-1] )*DSINC;
          wk2 = ( CADJ[IM-1] + CADJ[I-1] )*SINC;
          com1 = EXPA.times(wk1);
          com2 = IEXPA.times(wk2);
          com = com1.plus(com2);
          com = com.times(0.5*DA);
          DZDC = DZDC.plus(com);
          wk1 = ( SADJ[I-1] - SADJ[IM-1] )*DSINC;
          wk2 = ( SADJ[IM-1] + SADJ[I-1] )*SINC;
          com1 = EXPA.times(wk1);
          com2 = IEXPA.times(wk2);
          com = com1.plus(com2);
          com = com.times(0.5*DA);
          DZDS = DZDS.plus(com);
          IM = I;
        }

        if ( 1.0 + Math.pow(Z[NFFTP-1].real(),2) + Math.pow(Z[NFFTP-1].imag(),2) == 1.0 ) { break; }

        com = DZDC.conjugate();
        com = com.times(DZDS);
        ARG = com.imag();
        //com = null;
        if ( ARG == 0.0 ) { return null; } // FAILED: NEED TO CATER FOR THE CASE ARG=0
        com = Z[NFFTP-1].conjugate();
        com = com.times(DZDS);
        DC = -com.imag() / ARG;
        com = DZDC.conjugate();
        com = com.times(Z[NFFTP-1]);
        DS = -com.imag() / ARG;
        DDZ = PI*( Math.pow(DC,2) + Math.pow(DS,2) ) + 2.0*Math.abs(DC*DS);
        ARG = Math.min( 1.0 , Z[NFFTP-1].abs() / DDZ );
        DC = ARG*DC;
        DS = ARG*DS;
        for ( I=1 ; I<=NFFTP ; I++ ) {
          A[I-1] = A[I-1] + DC*CADJ[I-1] + DS*SADJ[I-1];
        }
        CVAL += DC;
        SVAL += DS;
        ITER += 1;
      }

      ZADJ = new Complex(0.0,0.0);
      for ( I=1 ; I<=NFFT ; I++ ) {
        ZADJ = ZADJ.plus(Z[I-1]);
      }
      ZADJ = ZADJ.times( 1.0 / NFFT );
      for ( I=1 ; I<=NFFT ; I++ ) {
        Z[I-1] = Z[I-1].minus(ZADJ);
      }
      Z[NFFTP-1] = Z[0];

      MyPoint2D p;
      MyPolygon coords = new MyPolygon();
      for ( I=1 ; I<=NFFTP ; I++ ) {
        p = new MyPoint2D( Z[I-1].real() , Z[I-1].imag() );
        coords.add(p);
      }
      return coords;

    }

}
