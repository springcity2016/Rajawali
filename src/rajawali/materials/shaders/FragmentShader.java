/**
 * Copyright 2013 Dennis Ippel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package rajawali.materials.shaders;

import java.util.List;

import rajawali.lights.ALight;
import android.opengl.GLES20;


public class FragmentShader extends AShader {
	private RFloat muColorInfluence;
	
	private RVec2 mvTextureCoord;
	@SuppressWarnings("unused")
	private RVec3 mvCubeTextureCoord;
	private RVec3 mvNormal;
	private RVec4 mvColor;
	
	private RVec4 mgColor;
	private RVec3 mgNormal;
	private RVec2 mgTextureCoord;
	
	private int muColorInfluenceHandle;
	private float mColorInfluence;
	
	@SuppressWarnings("unused")
	private List<ALight> mLights;
	private boolean mHasCubeMaps;
	private boolean mTimeEnabled;
	
	public FragmentShader()
	{
		super(ShaderType.FRAGMENT);
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		
		addPrecisionSpecifier(DataType.FLOAT, Precision.MEDIUMP);
		
		// -- uniforms
		
		muColorInfluence = (RFloat) addUniform(DefaultShaderVar.U_COLOR_INFLUENCE);
		if(mTimeEnabled)
			addUniform(DefaultShaderVar.U_TIME);
		
		// -- varyings
		
		mvTextureCoord = (RVec2) addVarying(DefaultShaderVar.V_TEXTURE_COORD);
		if(mHasCubeMaps)
			mvCubeTextureCoord = (RVec3) addVarying(DefaultShaderVar.V_CUBE_TEXTURE_COORD);
		mvNormal = (RVec3) addVarying(DefaultShaderVar.V_NORMAL);
		mvColor = (RVec4) addVarying(DefaultShaderVar.V_COLOR);
		addVarying(DefaultShaderVar.V_EYE_DIR);
		
		// -- globals
		
		mgColor = (RVec4) addGlobal(DefaultShaderVar.G_COLOR);
		mgNormal = (RVec3) addGlobal(DefaultShaderVar.G_NORMAL);
		mgTextureCoord = (RVec2) addGlobal(DefaultShaderVar.G_TEXTURE_COORD);
	}
	
	@Override
	public void main() {
		mgNormal.assign(normalize(mvNormal));
		mgTextureCoord.assign(mvTextureCoord);		
		mgColor.assign(muColorInfluence.multiply(mvColor));
		
		for(int i=0; i<mShaderFragments.size(); i++)
		{
			IShaderFragment fragment = mShaderFragments.get(i);
			fragment.setStringBuilder(mShaderSB);
			fragment.main();
		}
		
		GL_FRAG_COLOR.assign(mgColor);
	}
	
	@Override
	public void applyParams()
	{
		super.applyParams();
		
		GLES20.glUniform1f(muColorInfluenceHandle, mColorInfluence);
	}
	
	@Override
	public void setLocations(int programHandle) {
		super.setLocations(programHandle);
		
		muColorInfluenceHandle = getUniformLocation(programHandle, DefaultShaderVar.U_COLOR_INFLUENCE);
	}
	
	public void setLights(List<ALight> lights)
	{
		mLights = lights;
	}
	
	public void setColorInfluence(float influence) {
		mColorInfluence = influence;
	}
	
	public float getColorInfluence() {
		return mColorInfluence;
	}

	public void hasCubeMaps(boolean value)
	{
		mHasCubeMaps = value;
	}
	
	public void enableTime(boolean value)
	{
		mTimeEnabled = value;
	}
}
