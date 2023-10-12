package jmorph.hangle;

import geometry.MyPolygon;
import jmorph.maths.Complex;
import jmorph.maths.ComplexVector;

/** Translation of Fortran code hangle by James Crampton.
 * @author Peter Lelievre
 */
@SuppressWarnings("PublicInnerClass")
public class HAngle {

    /** The class definition for the object returned by the run method. */
    @SuppressWarnings("PublicField")
    public static class Out {
        public String message = null; /** A warning message (null if no error occurs). */
        public double length = -1.0; /** The outline length. */
        public ComplexVector coeffs = null; /** The coefficients from the HAngle outline analysis. */
    }

    /** The only method in the class.
     * @param coords The outline coordinates.
     * @param ind The INDEX input for the hangle program.
     * @return Holds a possible warning message, outline length and coefficients from the outline analysis.
     */
    public static Out run( MyPolygon coords, int ind ) {

      String outMessage = null;

      final int NMAXM=9999, NMAX=NMAXM+1, NFFT=(int)Math.pow(2,10), NFFTP=NFFT+1, NFFT2=NFFT/2;

      double[] XVAL = new double[NMAX];
      double[] YVAL = new double[NMAX];
      double[] AVAL = new double[NMAX];
      double[] AMEAN = new double[NMAX];
      double[] SVAL = new double[NMAX];
      double[] DSVAL = new double[NMAX];
      double[] DDA = new double[NMAXM];
      double[] AOUT = new double[NFFT];
      double[] DEQV = new double[2*NFFT];
      Complex[] DOUT = new Complex[NFFT];
      double[] POS = new double[5];
      double[] W = new double[5];
      // EQUIVALENCE (DOUT,DEQV)

      double HPI = 2.0*Math.atan(1.0);
      double PI = 2.0*HPI;
      double PI2 = 2.0*PI;
      double CONV = 180.0/PI;

      double R3_7 = Math.sqrt(3.0/7.0);
      POS[0] = 0.0;
      POS[1] = 0.5*(1.0-R3_7);
      POS[2] = 0.5;
      POS[3] = 0.5*(1.0+R3_7);
      POS[4] = 1.0;
      W[0] = 1.0/20.0;
      W[1] = 49.0/180.0;
      W[2] = 16.0/45.0;
      W[3] = W[1];
      W[4] = W[0];

      int NCOEFF = NFFT2;
      int INDEX = ind; // previously hardwired to 0

      // SUBROUTINE SUBFIT

      Complex ZM,ZP;

      int N = 1;
      while (true) {
          if (N>coords.size()) { break; }
          XVAL[N-1] = coords.get(N-1).getX();
          YVAL[N-1] = coords.get(N-1).getY();
          N += 1;
      }
      XVAL[N-1] = XVAL[0];
      YVAL[N-1] = YVAL[0];
      int NM = N-1;

      int IM = NM;
      int I = 1;
      double DX = XVAL[0] - XVAL[IM-1];
      double DY = YVAL[0] - YVAL[IM-1];
      DSVAL[0] = Math.hypot(DX,DY);
      ZM = new Complex( DX/DSVAL[0] , DY/DSVAL[0] );
      AMEAN[0] = Math.atan2(DY,DX);
      for ( int IP=2 ; IP<=N ; IP++ ) {
        DX = XVAL[IP-1] - XVAL[I-1];
        DY = YVAL[IP-1] - YVAL[I-1];
        DSVAL[IP-1] = Math.hypot(DX,DY);
        ZP = new Complex( DX/DSVAL[IP-1] , DY/DSVAL[IP-1] );
        ZM = ZP.divide(ZM);
        double DA = Math.atan2(ZM.imag(),ZM.real());
        AMEAN[IP-1] = AMEAN[I-1] + DA;
        AVAL[I-1] = AMEAN[I-1] + 0.5*DA + Math.atan(Math.tan(0.5*DA)*(DSVAL[I-1]-DSVAL[IP-1])/(DSVAL[I-1]+DSVAL[IP-1]));
        AVAL[I-1] = Math.max( AVAL[I-1] , Math.max(AMEAN[I-1],AMEAN[IP-1]) - HPI );
        AVAL[I-1] = Math.min( AVAL[I-1] , Math.min(AMEAN[I-1],AMEAN[IP-1]) + HPI );
        I = IP;
        ZM = ZP;
      }
      AVAL[N-1] = AVAL[0] + (AMEAN[N-1]-AMEAN[0]);

      SVAL[0] = 0.0;
      I = 1;
      for ( int IP=2 ; IP<=N ; IP++ ) {
        // SUBROUTINE FITSPL
        double DA0 = AVAL[I-1]  - AMEAN[IP-1];
        double DA1 = AVAL[IP-1] - AMEAN[IP-1];
        double DD = 6.0*(DA0+DA1);
        while (true) {
          double S = 0.0;
          double DSDD = 0.0;
          for ( int J=1 ; J<=5 ; J++ ) {
            double T1 = POS[J-1];
            double T0 = 1.0 - T1;
            double HPROD = 0.5*T1*T0;
            double DA = DA0*T0 + DA1*T1 - DD*HPROD;
            S += W[J-1]*Math.sin(DA);
            DSDD -= W[J-1]*HPROD*Math.cos(DA);
          }
          if ( 1.0 + Math.pow(S,2) == 1.0) { break; }
          DD -= (S/DSDD)*Math.min( 1.0 , 124.0*Math.pow(DSDD,2)/Math.abs(S));
        }
        DDA[I-1] = DD;
        double C = 0.0;
        for ( int J=1 ; J<=5 ; J++ ) {
          double T1 = POS[J-1];
          double T0 = 1.0 - T1;
          double HPROD = 0.5*T1*T0;
          double DA = DA0*T0 + DA1*T1 - DD*HPROD;
          C += W[J-1]*Math.cos(DA);
        }
        DSVAL[IP-1] /= C;
        // END SUBROUTINE FITSPLINE
        SVAL[IP-1] = SVAL[I-1] + DSVAL[IP-1];
        I = IP;
      }

      double DSOUT = SVAL[N-1] / NFFT;
      int I0 = 1;
      int I1 = 2;
      for ( I=1 ; I<=NFFT ; I++ ) {
        double SOUT = DSOUT * (I-1);
        while ( SOUT >= SVAL[I1-1] ) {
          I0 = I1;
          I1 += 1;
        }
        double TOUT = (SOUT-SVAL[I0-1]) / DSVAL[I1-1];
        // SUBROUTINE GETSPL
        Complex DZ;
        double DA0 = AVAL[I0-1] - AMEAN[I1-1];
        double DA1 = AVAL[I1-1] - AMEAN[I1-1];
        double DD = DDA[I0-1];
        double C = 0.0;
        double S = 0.0;
        double DA = 0.0;
        for ( int J=1 ; J<=5 ; J++ ) {
          double T1 = TOUT*POS[J-1];
          double T0 = 1.0 - T1;
          double HPROD = 0.5*T1*T0;
          DA = DA0*T0 + DA1*T1 - DD*HPROD;
          C += W[J-1]*Math.cos(DA);
          S += W[J-1]*Math.sin(DA);
        }
        C = TOUT*C;
        S = TOUT*S;
        DX = DSVAL[I1-1]*C;
        DY = DSVAL[I1-1]*S;
        Complex DZ1 = new Complex(DX,DY);
        Complex DZ2 = new Complex( Math.cos(AMEAN[I1-1]) , Math.sin(AMEAN[I1-1]) );
        DZ = DZ1.times(DZ2);
        double XOUT = XVAL[I0-1] + DZ.real();
        double YOUT = YVAL[I0-1] + DZ.imag();
        AOUT[I-1] = AMEAN[I1-1] + DA;
        // END SUBROUTINE GETSPL
      }

      int K0 = (int)Math.round( (AVAL[N-1]-AVAL[0]) / PI2 ); // K0=NINT((AVAL(N)-AVAL(1))/PI2)
      if ( Math.abs(K0) != 1 ) { return new Out(); } // APPROACH WILL FAIL AS THIS IS NOT A SIMPLE LOOP

      if ( K0==-1 ) {
        for ( I=1 ; I<=NFFT ; I++ ) {
          AOUT[I-1] = -AOUT[I-1];
        }
      }

      // END SUBROUTINE SUBFIT

      double outlineLength = SVAL[N-1]; // the outline length

      // SUBROUTINE SUBPOS

      Complex CMOV;

      double RNORM = PI2 / NFFT;
      for ( I=1 ; I<=NFFT ; I++ ) {
        DOUT[I-1] = new Complex( AOUT[I-1]-RNORM*(I-1) , 0.0 );
      }

      // Have to deal with DOUT,DEQV equivalence:
      for ( int k=1 ; k<=NFFT ; k++ ) {
          DEQV[2*(k-1)  ] = DOUT[k-1].real();
          DEQV[2*(k-1)+1] = DOUT[k-1].imag();
      }

      // SUBROUTINE FOUR1
      
      double WR,WI,WPR,WPI,WTEMP,THETA;
      N = 2*NFFT;
      int J = 1;
      for ( I=1 ; I<=N ; I+=2 ) {
        if (J>I) {
          double TEMPR = DEQV[J-1];
          double TEMPI = DEQV[J];
          DEQV[J-1] = DEQV[I-1];
          DEQV[J] = DEQV[I];
          DEQV[I-1] = TEMPR;
          DEQV[I] = TEMPI;
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
        THETA = 6.28322530719959 / (-MMAX);
        WPR = -2.0*Math.pow( Math.sin(0.5*THETA) , 2 );
        WPI = Math.sin(THETA);
        WR = 1.0;
        WI = 0.0;
        for ( int M=1 ; M<=MMAX ; M+=2 ) {
          for ( I=M ; I<=N ; I+=ISTEP ) {
            J = I + MMAX;
            double TEMPR = WR*DEQV[J-1] - WI*DEQV[J];
            double TEMPI = WR*DEQV[J] + WI*DEQV[J-1];
            DEQV[J-1] = DEQV[I-1] - TEMPR;
            DEQV[J] = DEQV[I] - TEMPI;
            DEQV[I-1] += TEMPR;
            DEQV[I] += TEMPI;
          }
          WTEMP = WR;
          WR = WR*WPR - WI*WPI + WR;
          WI = WI*WPR + WTEMP*WPI + WI;
        }
        MMAX = ISTEP;
      }

      // END SUBROUTINE FOUR1

      // Have to deal with DOUT,DEQV equivalence:
      for ( int k=1 ; k<=NFFT ; k++ ) {
          double re = DEQV[2*(k-1)  ];
          double im = DEQV[2*(k-1)+1];
          DOUT[k-1] = new Complex(re,im);
      }

      RNORM = 1.0 / NFFT;
      for ( I=1 ; I<=NFFT ; I++ ) {
        DOUT[I-1] = DOUT[I-1].times(RNORM);
      }

      if (INDEX==0) {
        CMOV = new Complex(1.0,0.0);
      } else {
        int IA = Math.abs(INDEX);
        int IP = IA + 1;
        if ( DOUT[IP].abs() == 0.0 ) {
          CMOV = new Complex(1.0,0.0);
          outMessage = "WARNING: HAVE KEPT INITIAL STARTING POINT BECAUSE COEFFICIENT TO BE USED IS ZERO";
        } else {
          if (INDEX<0) {
            CMOV = new Complex( 0.0 , DOUT[IP].abs() );
          } else {
            CMOV = new Complex( 0.0 , -DOUT[IP].abs() );
          }
          CMOV = CMOV.divide(DOUT[IP]);
          double ARG = Math.atan2( CMOV.imag() , CMOV.real() ) / IA;
          CMOV = new Complex( Math.cos(ARG) , Math.sin(ARG) );
        }
      }
      for ( I=3 ; I<=NCOEFF ; I++ ) {
        DOUT[I-1] = DOUT[I-1].times( CMOV.pow(I-1) );
      }

      Out out = new Out();
      out.message = outMessage;
      out.coeffs = new ComplexVector();
      for ( I=3 ; I<=NCOEFF ; I++ ) {
        Complex com = new Complex( DOUT[I-1].real() , DOUT[I-1].imag() );
        out.coeffs.add(com);
      }
      out.length = outlineLength; // the outline length
      return out;

      // END SUBROUTINE SUBPOS

    }

}
