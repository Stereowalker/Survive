package com.stereowalker.survive.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StinkParticle extends TextureSheetParticle {
   private final double stinkPosX;
   private final double stinkPosY;
   private final double stinkPosZ;

   protected StinkParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
      super(world, x, y, z);
      this.xd = xd;
      this.yd = yd;
      this.zd = zd;
      this.x = x;
      this.y = y;
      this.z = z;
      this.stinkPosX = this.x;
      this.stinkPosY = this.y;
      this.stinkPosZ = this.z;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = f * 0.9F;
      this.gCol = f * 0.3F;
      this.bCol = f;
      this.lifetime = (int)(Math.random() * 10.0D) + 40;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @Override
   public void move(double x, double y, double z) {
      this.setBoundingBox(this.getBoundingBox().move(x, y, z));
      this.setLocationFromBoundingbox();
   }

   @Override
   public float getQuadSize(float scaleFactor) {
      float f = ((float)this.age + scaleFactor) / (float)this.lifetime;
      f = 1.0F - f;
      f = f * f;
      f = 1.0F - f;
      return this.quadSize * f;
   }

   @Override
   public int getLightColor(float partialTick) {
      int i = super.getLightColor(partialTick);
      float f = (float)this.age / (float)this.lifetime;
      f = f * f;
      f = f * f;
      int j = i & 255;
      int k = i >> 16 & 255;
      k = k + (int)(f * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
      }

      return j | k << 16;
   }

   @Override
   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float f = (float)this.age / (float)this.lifetime;
         float f1 = -f + f * f * 2.0F;
         float f2 = 1.0F - f1;
         this.x = this.stinkPosX + this.xd * (double)f2;
         this.y = this.stinkPosY + this.yd * (double)f2 + (double)(1.0F - f);
         this.z = this.stinkPosZ + this.zd * (double)f2;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      @Override
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         StinkParticle portalparticle = new StinkParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         portalparticle.pickSprite(this.spriteSet);
         return portalparticle;
      }
   }
}