package p455w0rd.danknull.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * @author p455w0rd
 *
 */
public class ModelDankNullDock extends ModelBase {
	//fields
	ModelRenderer base;
	ModelRenderer base2;
	ModelRenderer base2edge1;
	ModelRenderer base2edge2;
	ModelRenderer base2edge3;
	ModelRenderer base2edge4;

	public ModelDankNullDock() {
		textureWidth = 64;
		textureHeight = 64;

		base = new ModelRenderer(this, 0, 0);
		base.addBox(0F, 0F, 0F, 16, 1, 16);
		base.setRotationPoint(-8F, 23F, -8F);
		base.setTextureSize(64, 64);
		base.mirror = true;
		setRotation(base, 0F, 0F, 0F);
		base2 = new ModelRenderer(this, 0, 18);
		base2.addBox(0F, 0F, 0F, 8, 1, 8);
		base2.setRotationPoint(-4F, 22F, -4F);
		base2.setTextureSize(64, 64);
		base2.mirror = true;
		setRotation(base2, 0F, 0F, 0F);
		base2edge1 = new ModelRenderer(this, 7, 18);
		base2edge1.addBox(0F, 0F, 0F, 1, 1, 8);
		base2edge1.setRotationPoint(-5F, 21F, -4F);
		base2edge1.setTextureSize(64, 64);
		base2edge1.mirror = true;
		setRotation(base2edge1, 0F, 0F, 0F);
		base2edge2 = new ModelRenderer(this, 7, 18);
		base2edge2.addBox(0F, 0F, 0F, 1, 1, 8);
		base2edge2.setRotationPoint(4F, 21F, -4F);
		base2edge2.setTextureSize(64, 64);
		base2edge2.mirror = true;
		setRotation(base2edge2, 0F, 0F, 0F);
		base2edge3 = new ModelRenderer(this, 7, 25);
		base2edge3.addBox(0F, 0F, 0F, 8, 1, 1);
		base2edge3.setRotationPoint(-4F, 21F, -5F);
		base2edge3.setTextureSize(64, 64);
		base2edge3.mirror = true;
		setRotation(base2edge3, 0F, 0F, 0F);
		base2edge4 = new ModelRenderer(this, 7, 25);
		base2edge4.addBox(0F, 0F, 0F, 8, 1, 1);
		base2edge4.setRotationPoint(-4F, 21F, 4F);
		base2edge4.setTextureSize(64, 64);
		base2edge4.mirror = true;
		setRotation(base2edge4, 0F, 0F, 0F);
	}

	public void render() {
		render(0.0625F);
	}

	public void render(final float scale) {
		render((Entity) null, 0f, 0f, 0f, 0f, 0f, scale);
	}

	@Override
	public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		base.render(f5);
		base2.render(f5);
		base2edge1.render(f5);
		base2edge2.render(f5);
		base2edge3.render(f5);
		base2edge4.render(f5);
	}

	private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}