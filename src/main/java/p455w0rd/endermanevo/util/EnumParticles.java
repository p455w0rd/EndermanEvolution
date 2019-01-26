/*
 * This file is part of Enderman Evolution.
 * Copyright (c) 2016, p455w0rd (aka TheRealp455w0rd), All rights reserved
 * unless
 * otherwise stated.
 *
 * Enderman Evolution is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * Enderman Evolution is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * MIT License for more details.
 *
 * You should have received a copy of the MIT License
 * along with Enderman Evolution. If not, see
 * <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.endermanevo.util;

/**
 * @author p455w0rd
 *
 */
public enum EnumParticles {

		PORTAL_GREEN("vanilla"), LOVE("vanilla"), PORTAL_RED("vanilla"), PORTAL("vanilla");

	String renderer;

	EnumParticles(String rendererIn) {
		renderer = rendererIn;
	}

	public String getRenderer() {
		return renderer;
	}
}