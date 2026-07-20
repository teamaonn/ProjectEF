package moze_intel.projecte.utils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.components.DataComponentProcessor;
import moze_intel.projecte.api.components.IDataComponentProcessor;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import org.jetbrains.annotations.Nullable;

/**
 * Discovers EMC mappers, recipe type mappers, and data component processors.
 * <p>
 * On Fabric there is no annotation scanning across mods, so classes are declared in each mod's {@code fabric.mod.json} under the custom keys
 * {@code projecte:emc_mappers}, {@code projecte:recipe_type_mappers}, and {@code projecte:data_component_processors} (each an array of class names). The classes
 * must still be annotated with the corresponding annotation, which supplies the priority and required mods, and may still use the {@code Instance} annotation on
 * a static field to provide a singleton instance.
 */
public class AnnotationHelper {

	private static final String EMC_MAPPERS_KEY = "projecte:emc_mappers";
	private static final String RECIPE_TYPE_MAPPERS_KEY = "projecte:recipe_type_mappers";
	private static final String DATA_COMPONENT_PROCESSORS_KEY = "projecte:data_component_processors";

	public static List<IDataComponentProcessor> getDataComponentProcessors() {
		List<IDataComponentProcessor> dataComponentProcessors = new ArrayList<>();
		Object2IntMap<IDataComponentProcessor> priorities = new Object2IntOpenHashMap<>();
		for (String className : findDeclaredClasses(DATA_COMPONENT_PROCESSORS_KEY)) {
			DataComponentProcessor annotation = getAnnotation(className, DataComponentProcessor.class);
			if (annotation != null && checkRequiredMods(className, annotation.requiredMods())) {
				IDataComponentProcessor processor = getDataComponentProcessor(className);
				if (processor != null) {
					int priority = annotation.priority();
					dataComponentProcessors.add(processor);
					priorities.put(processor, priority);
					PECore.debugLog("Found and loaded Data Component Processor: {}, with priority {}", processor.getName(), priority);
				}
			}
		}
		dataComponentProcessors.sort(Comparator.comparingInt(priorities::getInt).reversed());
		return dataComponentProcessors;
	}

	public static List<IRecipeTypeMapper> getRecipeTypeMappers() {
		List<IRecipeTypeMapper> recipeTypeMappers = new ArrayList<>();
		Object2IntMap<IRecipeTypeMapper> priorities = new Object2IntOpenHashMap<>();
		for (String className : findDeclaredClasses(RECIPE_TYPE_MAPPERS_KEY)) {
			RecipeTypeMapper annotation = getAnnotation(className, RecipeTypeMapper.class);
			if (annotation != null && checkRequiredMods(className, annotation.requiredMods())) {
				IRecipeTypeMapper mapper = getRecipeTypeMapper(className);
				if (mapper != null) {
					int priority = annotation.priority();
					recipeTypeMappers.add(mapper);
					priorities.put(mapper, priority);
					PECore.debugLog("Found and loaded RecipeType Mapper: {}, with priority {}", mapper.getName(), priority);
				}
			}
		}
		recipeTypeMappers.sort(Comparator.comparingInt(priorities::getInt).reversed());
		return recipeTypeMappers;
	}

	//Note: We don't bother caching this value because EMCMappingHandler#loadMappers caches our processed result
	@SuppressWarnings("unchecked")
	public static List<IEMCMapper<NormalizedSimpleStack, Long>> getEMCMappers() {
		List<IEMCMapper<NormalizedSimpleStack, Long>> emcMappers = new ArrayList<>();
		Object2IntMap<IEMCMapper<NormalizedSimpleStack, Long>> priorities = new Object2IntOpenHashMap<>();
		for (String className : findDeclaredClasses(EMC_MAPPERS_KEY)) {
			EMCMapper annotation = getAnnotation(className, EMCMapper.class);
			if (annotation != null && checkRequiredMods(className, annotation.requiredMods())) {
				IEMCMapper<?, ?> mapper = getEMCMapper(className);
				if (mapper != null) {
					try {
						IEMCMapper<NormalizedSimpleStack, Long> emcMapper = (IEMCMapper<NormalizedSimpleStack, Long>) mapper;
						int priority = annotation.priority();
						emcMappers.add(emcMapper);
						priorities.put(emcMapper, priority);
						PECore.debugLog("Found and loaded EMC mapper: {}, with priority {}", mapper.getName(), priority);
					} catch (ClassCastException e) {
						PECore.LOGGER.error("{}: Is not a mapper for {}, to {}", mapper.getClass(), NormalizedSimpleStack.class, Long.class, e);
					}
				}
			}
		}
		emcMappers.sort(Comparator.comparingInt(priorities::getInt).reversed());
		return emcMappers;
	}

	private static List<String> findDeclaredClasses(String customKey) {
		List<String> classNames = new ArrayList<>();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			CustomValue value = mod.getMetadata().getCustomValue(customKey);
			if (value != null) {
				if (value.getType() == CustomValue.CvType.ARRAY) {
					for (CustomValue entry : value.getAsArray()) {
						if (entry.getType() == CustomValue.CvType.STRING) {
							classNames.add(entry.getAsString());
						} else {
							PECore.LOGGER.error("Mod {} declared a non string entry in its {} array", mod.getMetadata().getId(), customKey);
						}
					}
				} else {
					PECore.LOGGER.error("Mod {} declared {} but it is not an array of class names", mod.getMetadata().getId(), customKey);
				}
			}
		}
		return classNames;
	}

	@Nullable
	private static <ANNOTATION extends Annotation> ANNOTATION getAnnotation(String className, Class<ANNOTATION> annotationClass) {
		try {
			//Load without initializing so that we can check the annotation before any static initializers may reference classes of missing mods
			Class<?> clazz = Class.forName(className, false, AnnotationHelper.class.getClassLoader());
			ANNOTATION annotation = clazz.getAnnotation(annotationClass);
			if (annotation == null) {
				PECore.LOGGER.error("Class {} is declared as a {} but is missing the annotation", className, annotationClass.getSimpleName());
			}
			return annotation;
		} catch (ClassNotFoundException | LinkageError e) {
			PECore.LOGGER.error("Failed to load declared class: {}", className, e);
			return null;
		}
	}

	@Nullable
	private static IEMCMapper<?, ?> getEMCMapper(String className) {
		return createOrGetInstance(className, IEMCMapper.class, EMCMapper.Instance.class, IEMCMapper::getName);
	}

	@Nullable
	private static IRecipeTypeMapper getRecipeTypeMapper(String className) {
		return createOrGetInstance(className, IRecipeTypeMapper.class, RecipeTypeMapper.Instance.class, IRecipeTypeMapper::getName);
	}

	@Nullable
	private static IDataComponentProcessor getDataComponentProcessor(String className) {
		return createOrGetInstance(className, IDataComponentProcessor.class, DataComponentProcessor.Instance.class, IDataComponentProcessor::getName);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	private static <T> T createOrGetInstance(String className, Class<T> baseClass, Class<? extends Annotation> instanceAnnotation, Function<T, String> nameFunction) {
		//Try to create an instance of the class
		try {
			Class<? extends T> subClass = Class.forName(className).asSubclass(baseClass);
			//First try looking at the fields of the class to see if one of them is specified as the instance
			Field[] fields = subClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(instanceAnnotation)) {
					if (Modifier.isStatic(field.getModifiers())) {
						try {
							Object fieldValue = field.get(null);
							if (baseClass.isInstance(fieldValue)) {
								T instance = (T) fieldValue;
								PECore.debugLog("Found specified {} instance for: {}. Using it rather than creating a new instance.", baseClass.getSimpleName(),
										nameFunction.apply(instance));
								return instance;
							} else {
								PECore.LOGGER.error("{} annotation found on non {} field: {}", instanceAnnotation.getSimpleName(), baseClass.getSimpleName(), field);
								return null;
							}
						} catch (IllegalAccessException e) {
							PECore.LOGGER.error("{} annotation found on inaccessible field: {}", instanceAnnotation.getSimpleName(), field);
							return null;
						}
					} else {
						PECore.LOGGER.error("{} annotation found on non static field: {}", instanceAnnotation.getSimpleName(), field);
						return null;
					}
				}
			}
			//If we don't have any fields that have the Instance annotation, then try to create a new instance of the class
			return subClass.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError | InvocationTargetException | NoSuchMethodException e) {
			PECore.LOGGER.error("Failed to load: {}", className, e);
		}
		return null;
	}

	private static boolean checkRequiredMods(String className, String[] requiredMods) {
		FabricLoader loader = FabricLoader.getInstance();
		for (String requiredMod : requiredMods) {
			if (!requiredMod.isEmpty() && !loader.isModLoaded(requiredMod)) {
				PECore.debugLog("Skipped checking class {}, as its required mods ({}) are not loaded.", className, Arrays.toString(requiredMods));
				return false;
			}
		}
		return true;
	}
}
