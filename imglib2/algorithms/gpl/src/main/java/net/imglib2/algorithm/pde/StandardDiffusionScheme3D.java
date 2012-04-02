package net.imglib2.algorithm.pde;

public class StandardDiffusionScheme3D {

	
	
	/**
	 * Real, symmetric diffusion tensor for 3D structures:
	 * <pre>
	 * Dxx Dxy Dxz		a d e
	 * Dyx Dyy Dyz	=	d b f
	 * Dzx Dzy Dzz		e f c
	 * </pre>
	 * @param U
	 * @param D
	 * @return
	 */
	protected final float diffusionScheme(float[] U, float[][] D) {
		
//		final float a = 
		
//		A2 = 0.5*(-f(:,:,nz)-f(:,py,:));
//		A4 = 0.5*( e(:,:,nz)+e(nx,:,:));
//		A5 = ( c(:,:,nz)+c);
//		A6 = 0.5*(-e(:,:,nz)-e(px,:,:));
//		A8 = 0.5*( f(:,:,nz)+f(:,ny,:));
//
//		B1 = 0.5*(-d(nx,:,:) -d(:,py,:));
//		B2 = (b(:,py,:)+b);
//		B3 = 0.5*(d(px,:,:)+ d(:,py,:));
//		B4 = (a(nx,:,:)+a);
//		B5 = - (a(nx,:,:) + 2*a + a(px,:,:)) ...
//		      -(b(:,ny,:) + 2*b + b(:,py,:)) ...
//		      -(c(:,:,nz) + 2*c + c(:,:,pz));
//		B6 = (a(px,:,:)+a);
//		B7 = 0.5*(d(nx,:,:)+d(:,ny,:));
//		B8 = (b(:,ny,:)+b);
//		B9 = 0.5*(-d(px,:,:)-d(:,ny,:));
//
//		C2 = 0.5*(f(:,:,pz) + f(:,py,:));
//		C4 = 0.5*(-e(:,:,pz)-e(nx,:,:));
//		C5 = (c(:,:,pz)+c);
//		C6 = 0.5*(e(:,:,pz)+e(px,:,:));
//		C8 = 0.5*(-f(:,:,pz)-f(:,ny,:));
//
//		% Perform the diffusion
//		u=u+ 0.5*dt.*(  ...
//		        A2.*(u(: ,py,nz )-u) + ...
//		        A4.*(u(nx ,: ,nz)-u) + ...
//		        A5.*(u(: ,: ,nz)-u)  + ... 
//		        A6.*(u(px,: ,nz)-u)  + ... 
//		        A8.*(u(: ,ny,nz)-u)  + ...
//		        B1.*(u(nx,py,: )-u)  + ...
//		        B2.*(u(: ,py,: )-u)  + ...
//		        B3.*(u(px,py,: )-u)  + ...
//		        B4.*(u(nx,: ,: )-u)  + ...
//		        B5.*(u(:,: ,: )-u)  + ...
//		        B6.*(u(px,: ,: )-u)  + ...
//		        B7.*(u(nx,ny,: )-u)  + ...
//		        B8.*(u(: ,ny,: )-u)  + ...
//		        B9.*(u(px,ny,: )-u)  + ...
//		        C2.*(u(: ,py,pz)-u)  + ...
//		        C4.*(u(nx,: ,pz)-u)  + ...
//		        C5.*(u(: ,: ,pz)-u)  + ...
//		        C6.*(u(px,: ,pz)-u)  + ...
//		        C8.*(u(: ,ny,pz)-u));
//		
		
		
		
		
		return 0f;
	}

	
}
