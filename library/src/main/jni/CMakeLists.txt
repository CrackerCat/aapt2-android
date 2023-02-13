cmake_minimum_required(VERSION 3.6)
project(aapt2 VERSION 1.0.0 LANGUAGES C CXX ASM)

set(CMAKE_CXX_STANDARD 17)

set(C_FLAGS "-Werror=format -fdata-sections -ffunction-sections -fno-exceptions -fno-rtti -fno-threadsafe-statics")
set(LINKER_FLAGS "-Wl,--hash-style=both")

if (NOT CMAKE_BUILD_TYPE STREQUAL "Debug")
    message("Builing Release...")

    set(C_FLAGS "${C_FLAGS} -O2 -fvisibility=hidden -fvisibility-inlines-hidden")
    set(LINKER_FLAGS "${LINKER_FLAGS} -Wl,-exclude-libs,ALL -Wl,--gc-sections")
    
    add_definitions(-DNDEBUG)
else()
    message("Builing Debug...")

    add_definitions(-DDEBUG)
endif ()

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${C_FLAGS}")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${C_FLAGS}")

set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} ${LINKER_FLAGS}")
set(CMAKE_MODULE_LINKER_FLAGS "${CMAKE_MODULE_LINKER_FLAGS} ${LINKER_FLAGS}")

add_definitions(-D_FILE_OFFSET_BITS=64)

find_library(android-lib android)
find_package(cxx REQUIRED CONFIG)

# zlib
add_library(zlib STATIC
    aosp/src/zlib/adler32.c
    aosp/src/zlib/compress.c
    aosp/src/zlib/cpu_features.c
    aosp/src/zlib/crc32.c
    aosp/src/zlib/deflate.c
    aosp/src/zlib/gzclose.c
    aosp/src/zlib/gzlib.c
    aosp/src/zlib/gzread.c
    aosp/src/zlib/gzwrite.c
    aosp/src/zlib/infback.c
    aosp/src/zlib/inffast.c
    aosp/src/zlib/inflate.c
    aosp/src/zlib/inftrees.c
    aosp/src/zlib/trees.c
    aosp/src/zlib/uncompr.c
    aosp/src/zlib/zutil.c)
target_compile_definitions(zlib PRIVATE -DHAVE_HIDDEN -DZLIB_CONST)
target_include_directories(zlib PRIVATE aosp/src/zlib)
target_include_directories(zlib PUBLIC aosp/src/zlib)

# expat
add_library(expat STATIC
    aosp/src/expat/lib/xmlparse.c
    aosp/src/expat/lib/xmlrole.c
    aosp/src/expat/lib/xmltok.c)
target_compile_options(expat PRIVATE -fno-strict-aliasing -include stdio.h)
target_compile_definitions(expat PRIVATE -DHAVE_EXPAT_CONFIG_H)
target_include_directories(expat PRIVATE aosp/src/expat/lib aosp/src/expat)
target_include_directories(expat PUBLIC aosp/src/expat/lib aosp/src/expat)

# png
add_library(png STATIC
    aosp/src/libpng/png.c
    aosp/src/libpng/pngerror.c
    aosp/src/libpng/pngget.c
    aosp/src/libpng/pngmem.c
    aosp/src/libpng/pngpread.c
    aosp/src/libpng/pngread.c
    aosp/src/libpng/pngrio.c
    aosp/src/libpng/pngrtran.c
    aosp/src/libpng/pngrutil.c
    aosp/src/libpng/pngset.c
    aosp/src/libpng/pngtrans.c
    aosp/src/libpng/pngwio.c
    aosp/src/libpng/pngwrite.c
    aosp/src/libpng/pngwtran.c
    aosp/src/libpng/pngwutil.c)
if(${ANDROID_ABI} STREQUAL "arm64-v8a" OR ${ANDROID_ABI} STREQUAL "armeabi-v7a")
    target_sources(png PRIVATE
        aosp/src/libpng/arm/arm_init.c
        aosp/src/libpng/arm/filter_neon_intrinsics.c
        aosp/src/libpng/arm/palette_neon_intrinsics.c
        aosp/src/libpng/arm/filter_neon.S)
elseif(${ANDROID_ABI} STREQUAL "x86_64" OR ${ANDROID_ABI} STREQUAL "x86")
    target_sources(png PRIVATE
        aosp/src/libpng/intel/intel_init.c
        aosp/src/libpng/intel/filter_sse2_intrinsics.c)
    target_compile_definitions(png PRIVATE -DPNG_INTEL_SSE_OPT=1)
endif()
target_include_directories(png PRIVATE aosp/src/libpng)
target_include_directories(png PUBLIC aosp/src/libpng)
    
link_libraries(cxx::cxx)

# protobuf
add_library(protobuf STATIC
    protobuf/src/google/protobuf/any_lite.cc
    protobuf/src/google/protobuf/arena.cc
    protobuf/src/google/protobuf/extension_set.cc
    protobuf/src/google/protobuf/generated_enum_util.cc
    protobuf/src/google/protobuf/generated_message_table_driven_lite.cc
    protobuf/src/google/protobuf/generated_message_util.cc
    protobuf/src/google/protobuf/implicit_weak_message.cc
    protobuf/src/google/protobuf/io/coded_stream.cc
    protobuf/src/google/protobuf/io/io_win32.cc
    protobuf/src/google/protobuf/io/strtod.cc
    protobuf/src/google/protobuf/io/zero_copy_stream.cc
    protobuf/src/google/protobuf/io/zero_copy_stream_impl.cc
    protobuf/src/google/protobuf/io/zero_copy_stream_impl_lite.cc
    protobuf/src/google/protobuf/message_lite.cc
    protobuf/src/google/protobuf/parse_context.cc
    protobuf/src/google/protobuf/repeated_field.cc
    protobuf/src/google/protobuf/stubs/bytestream.cc
    protobuf/src/google/protobuf/stubs/common.cc
    protobuf/src/google/protobuf/stubs/int128.cc
    protobuf/src/google/protobuf/stubs/status.cc
    protobuf/src/google/protobuf/stubs/statusor.cc
    protobuf/src/google/protobuf/stubs/stringpiece.cc
    protobuf/src/google/protobuf/stubs/stringprintf.cc
    protobuf/src/google/protobuf/stubs/structurally_valid.cc
    protobuf/src/google/protobuf/stubs/strutil.cc
    protobuf/src/google/protobuf/stubs/time.cc
    protobuf/src/google/protobuf/wire_format_lite.cc
    protobuf/src/google/protobuf/any.cc
    protobuf/src/google/protobuf/any.pb.cc
    protobuf/src/google/protobuf/api.pb.cc
    protobuf/src/google/protobuf/compiler/importer.cc
    protobuf/src/google/protobuf/compiler/parser.cc
    protobuf/src/google/protobuf/descriptor.cc
    protobuf/src/google/protobuf/descriptor.pb.cc
    protobuf/src/google/protobuf/descriptor_database.cc
    protobuf/src/google/protobuf/duration.pb.cc
    protobuf/src/google/protobuf/dynamic_message.cc
    protobuf/src/google/protobuf/empty.pb.cc
    protobuf/src/google/protobuf/extension_set_heavy.cc
    protobuf/src/google/protobuf/field_mask.pb.cc
    protobuf/src/google/protobuf/generated_message_reflection.cc
    protobuf/src/google/protobuf/generated_message_table_driven.cc
    protobuf/src/google/protobuf/io/gzip_stream.cc
    protobuf/src/google/protobuf/io/printer.cc
    protobuf/src/google/protobuf/io/tokenizer.cc
    protobuf/src/google/protobuf/map_field.cc
    protobuf/src/google/protobuf/message.cc
    protobuf/src/google/protobuf/reflection_ops.cc
    protobuf/src/google/protobuf/service.cc
    protobuf/src/google/protobuf/source_context.pb.cc
    protobuf/src/google/protobuf/struct.pb.cc
    protobuf/src/google/protobuf/stubs/mathlimits.cc
    protobuf/src/google/protobuf/stubs/substitute.cc
    protobuf/src/google/protobuf/text_format.cc
    protobuf/src/google/protobuf/timestamp.pb.cc
    protobuf/src/google/protobuf/type.pb.cc
    protobuf/src/google/protobuf/unknown_field_set.cc
    protobuf/src/google/protobuf/util/delimited_message_util.cc
    protobuf/src/google/protobuf/util/field_comparator.cc
    protobuf/src/google/protobuf/util/field_mask_util.cc
    protobuf/src/google/protobuf/util/internal/datapiece.cc
    protobuf/src/google/protobuf/util/internal/default_value_objectwriter.cc
    protobuf/src/google/protobuf/util/internal/error_listener.cc
    protobuf/src/google/protobuf/util/internal/field_mask_utility.cc
    protobuf/src/google/protobuf/util/internal/json_escaping.cc
    protobuf/src/google/protobuf/util/internal/json_objectwriter.cc
    protobuf/src/google/protobuf/util/internal/json_stream_parser.cc
    protobuf/src/google/protobuf/util/internal/object_writer.cc
    protobuf/src/google/protobuf/util/internal/proto_writer.cc
    protobuf/src/google/protobuf/util/internal/protostream_objectsource.cc
    protobuf/src/google/protobuf/util/internal/protostream_objectwriter.cc
    protobuf/src/google/protobuf/util/internal/type_info.cc
    protobuf/src/google/protobuf/util/internal/type_info_test_helper.cc
    protobuf/src/google/protobuf/util/internal/utility.cc
    protobuf/src/google/protobuf/util/json_util.cc
    protobuf/src/google/protobuf/util/message_differencer.cc
    protobuf/src/google/protobuf/util/time_util.cc
    protobuf/src/google/protobuf/util/type_resolver_util.cc
    protobuf/src/google/protobuf/wire_format.cc
    protobuf/src/google/protobuf/wrappers.pb.cc)
target_compile_definitions(protobuf PRIVATE -DHAVE_ZLIB=1)
target_include_directories(protobuf PRIVATE 
    protobuf/android
    aosp/src/protobuf/src)
target_include_directories(protobuf PUBLIC aosp/src/protobuf/src)
target_link_libraries(protobuf log z)

# fmt
add_subdirectory(aosp/src/fmtlib)

include_directories(
    aosp/src/libsystem/include
    aosp/src/libutils/include
    aosp/src/libcutils/include)
    
# base
add_library(base STATIC
    aosp/src/libbase/errors_unix.cpp
    aosp/src/libbase/abi_compatibility.cpp
    aosp/src/libbase/chrono_utils.cpp
	aosp/src/libbase/cmsg.cpp
	aosp/src/libbase/file.cpp
	aosp/src/libbase/hex.cpp
	aosp/src/libbase/logging.cpp
	aosp/src/libbase/mapped_file.cpp
	aosp/src/libbase/parsebool.cpp
	aosp/src/libbase/parsenetaddress.cpp
	aosp/src/libbase/posix_strerror_r.cpp
	aosp/src/libbase/process.cpp
	aosp/src/libbase/properties.cpp
	aosp/src/libbase/stringprintf.cpp
	aosp/src/libbase/strings.cpp
	aosp/src/libbase/threads.cpp
	aosp/src/libbase/test_utils.cpp)
target_include_directories(base PUBLIC
    aosp/src/libbase/include
    aosp/src/liblog/include)

# utils
add_library(utils STATIC
    aosp/src/libutils/Errors.cpp
	aosp/src/libutils/FileMap.cpp
	aosp/src/libutils/JenkinsHash.cpp
	aosp/src/libutils/LightRefBase.cpp
	aosp/src/libutils/NativeHandle.cpp
	aosp/src/libutils/Printer.cpp
	aosp/src/libutils/RefBase.cpp
	aosp/src/libutils/SharedBuffer.cpp
	aosp/src/libutils/StopWatch.cpp
	aosp/src/libutils/String8.cpp
	aosp/src/libutils/String16.cpp
	aosp/src/libutils/StrongPointer.cpp
	aosp/src/libutils/SystemClock.cpp
	aosp/src/libutils/Threads.cpp
	aosp/src/libutils/Timers.cpp
	aosp/src/libutils/Tokenizer.cpp
	aosp/src/libutils/Unicode.cpp
	aosp/src/libutils/VectorImpl.cpp
	aosp/src/libutils/misc.cpp)
target_include_directories(utils PUBLIC
    aosp/src/liblog/include 
    aosp/src/libbase/include)

# cutils
add_library(cutils STATIC
    aosp/src/libcutils/socket_inaddr_any_server_unix.cpp
	aosp/src/libcutils/socket_local_client_unix.cpp
	aosp/src/libcutils/socket_local_server_unix.cpp
	aosp/src/libcutils/socket_network_client_unix.cpp
	aosp/src/libcutils/sockets_unix.cpp
	aosp/src/libcutils/sockets.cpp
	aosp/src/libcutils/fs.cpp
	aosp/src/libcutils/hashmap.cpp
	aosp/src/libcutils/multiuser.cpp
	aosp/src/libcutils/str_parms.cpp
	aosp/src/libcutils/ashmem-host.cpp
	aosp/src/libcutils/canned_fs_config.cpp
	aosp/src/libcutils/fs_config.cpp
	aosp/src/libcutils/trace-host.cpp
	aosp/src/libcutils/config_utils.cpp
	aosp/src/libcutils/iosched_policy.cpp
	aosp/src/libcutils/load_file.cpp
	aosp/src/libcutils/native_handle.cpp
	aosp/src/libcutils/properties.cpp
	aosp/src/libcutils/record_stream.cpp
	aosp/src/libcutils/strlcpy.c
	aosp/src/libcutils/threads.cpp)
target_compile_definitions(cutils PRIVATE -D_GNU_SOURCE)
target_include_directories(cutils PUBLIC
    aosp/src/libbase/include
    aosp/src/liblog/include)

# log
add_library(log STATIC
    aosp/src/liblog/log_event_list.cpp
    aosp/src/liblog/log_event_write.cpp
    aosp/src/liblog/logger_name.cpp
    aosp/src/liblog/logger_read.cpp
    aosp/src/liblog/logger_write.cpp
    aosp/src/liblog/properties.cpp
    aosp/src/liblog/logprint.cpp)
target_compile_definitions(log PRIVATE -DLIBLOG_LOG_TAG=1006 -D_XOPEN_SOURCE=700 -DFAKE_LOG_DEVICE=1)
target_include_directories(log PUBLIC
    aosp/src/liblog/include
    aosp/src/libbase/include)

# ziparchive
add_library(ziparchive STATIC
    aosp/src/libziparchive/zip_archive.cc
    aosp/src/libziparchive/zip_archive_stream_entry.cc
    aosp/src/libziparchive/zip_cd_entry_map.cc
    aosp/src/libziparchive/zip_writer.cc
    aosp/src/libziparchive/zip_error.cpp
    aosp/src/libziparchive/incfs_support/signal_handling.cpp)
target_compile_options(ziparchive PRIVATE -std=c++20)
set_property(TARGET ziparchive PROPERTY CXX_STANDARD 20)
target_compile_definitions(ziparchive PRIVATE -DZLIB_CONST -DZIPARCHIVE_DISABLE_CALLBACK_API=1)
target_include_directories(ziparchive PUBLIC
    aosp/src/libziparchive/include
    aosp/src/libziparchive/incfs_support/include
    aosp/src/libbase/include 
    aosp/src/liblog/include
    aosp/src/googletest/googletest/include)
    
# incfs
add_library(incfs STATIC aosp/src/incfs/util/map_ptr.cpp)
target_include_directories(incfs PUBLIC
    aosp/src/incfs/util/include 
    aosp/src/libbase/include
    aosp/src/liblog/include)

# androidfw
add_library(androidfw STATIC
    aosp/src/androidfw/ApkAssets.cpp
	aosp/src/androidfw/Asset.cpp
	aosp/src/androidfw/AssetDir.cpp
	aosp/src/androidfw/AssetManager.cpp
	aosp/src/androidfw/AssetManager2.cpp
	aosp/src/androidfw/AssetsProvider.cpp
	aosp/src/androidfw/AttributeResolution.cpp
	aosp/src/androidfw/ChunkIterator.cpp
	aosp/src/androidfw/ConfigDescription.cpp
	aosp/src/androidfw/Idmap.cpp
	aosp/src/androidfw/LoadedArsc.cpp
	aosp/src/androidfw/Locale.cpp
	aosp/src/androidfw/LocaleData.cpp
	aosp/src/androidfw/misc.cpp
	aosp/src/androidfw/ObbFile.cpp
	aosp/src/androidfw/PosixUtils.cpp
	aosp/src/androidfw/ResourceTypes.cpp
	aosp/src/androidfw/ResourceUtils.cpp
	aosp/src/androidfw/StreamingZipInflater.cpp
	aosp/src/androidfw/TypeWrappers.cpp
	aosp/src/androidfw/Util.cpp
	aosp/src/androidfw/ZipFileRO.cpp
	aosp/src/androidfw/ZipUtils.cpp)
target_compile_definitions(androidfw PUBLIC -DSTATIC_ANDROIDFW_FOR_TOOLS)
target_compile_definitions(androidfw PRIVATE -D_GNU_SOURCE -DNDEBUG)
target_include_directories(androidfw PUBLIC
    aosp/src/androidfw/include
    aosp/src/liblog/include
    aosp/src/libbase/include
    aosp/src/libziparchive/include
    aosp/src/incfs/util/include
    aosp/src/fmtlib/include)
target_link_libraries(androidfw PUBLIC fmt::fmt)


# ========================= aapt2 proto ============================
set(AAPT2_PROTO_SRC)  # proto source files
set(AAPT2_PROTO_HDRS) # proto head files
set(AAPT2_PROTO_DIR ${PROJECT_SOURCE_DIR}/aosp/src/aapt2)

file(GLOB_RECURSE PROTO_FILES ${AAPT2_PROTO_DIR}/*.proto)

foreach(proto ${PROTO_FILES})
    get_filename_component(FIL_WE ${proto} NAME_WE)
    
    set(TARGET_CPP_FILE "${AAPT2_PROTO_DIR}/${FIL_WE}.pb.cc")
    set(TARGET_HEAD_FILE "${AAPT2_PROTO_DIR}/${FIL_WE}.pb.h")
    
    if(EXISTS ${TARGET_CPP_FILE} AND EXISTS ${TARGET_HEAD_FILE})
        list(APPEND AAPT2_PROTO_SRC ${TARGET_CPP_FILE})
        list(APPEND AAPT2_PROTO_HDRS ${TARGET_HEAD_FILE})
    endif()
endforeach()

set_source_files_properties(${AAPT2_PROTO_SRC} PROPERTIES GENERATED TRUE)
set_source_files_properties(${AAPT2_PROTO_HDRS} PROPERTIES GENERATED TRUE)
# ========================= aapt2 proto ============================

add_library(aapt2 SHARED
    aosp/src/aapt2/cmd/Command.cpp
    aosp/src/aapt2/cmd/Compile.cpp
    aosp/src/aapt2/cmd/Convert.cpp
    aosp/src/aapt2/cmd/Diff.cpp
    aosp/src/aapt2/cmd/Dump.cpp
    aosp/src/aapt2/cmd/Link.cpp
    aosp/src/aapt2/cmd/Optimize.cpp
    aosp/src/aapt2/cmd/Util.cpp
    aosp/src/aapt2/compile/IdAssigner.cpp
    aosp/src/aapt2/compile/InlineXmlFormatParser.cpp
    aosp/src/aapt2/compile/NinePatch.cpp
    aosp/src/aapt2/compile/Png.cpp
    aosp/src/aapt2/compile/PngChunkFilter.cpp
    aosp/src/aapt2/compile/PngCrunch.cpp
    aosp/src/aapt2/compile/PseudolocaleGenerator.cpp
    aosp/src/aapt2/compile/Pseudolocalizer.cpp
    aosp/src/aapt2/compile/XmlIdCollector.cpp
    aosp/src/aapt2/configuration/ConfigurationParser.cpp
    aosp/src/aapt2/dump/DumpManifest.cpp
    aosp/src/aapt2/filter/AbiFilter.cpp
    aosp/src/aapt2/filter/ConfigFilter.cpp
    aosp/src/aapt2/format/Archive.cpp
    aosp/src/aapt2/format/Container.cpp
    aosp/src/aapt2/format/binary/BinaryResourceParser.cpp
    aosp/src/aapt2/format/binary/ResChunkPullParser.cpp
    aosp/src/aapt2/format/binary/TableFlattener.cpp
    aosp/src/aapt2/format/binary/XmlFlattener.cpp
    aosp/src/aapt2/format/proto/ProtoDeserialize.cpp
    aosp/src/aapt2/format/proto/ProtoSerialize.cpp
    aosp/src/aapt2/io/BigBufferStream.cpp
    aosp/src/aapt2/io/File.cpp
    aosp/src/aapt2/io/FileStream.cpp
    aosp/src/aapt2/io/FileSystem.cpp
    aosp/src/aapt2/io/StringStream.cpp
    aosp/src/aapt2/io/Util.cpp
    aosp/src/aapt2/io/ZipArchive.cpp
    aosp/src/aapt2/link/AutoVersioner.cpp
    aosp/src/aapt2/link/ManifestFixer.cpp
    aosp/src/aapt2/link/NoDefaultResourceRemover.cpp
    aosp/src/aapt2/link/ProductFilter.cpp
    aosp/src/aapt2/link/PrivateAttributeMover.cpp
    aosp/src/aapt2/link/ReferenceLinker.cpp
    aosp/src/aapt2/link/ResourceExcluder.cpp
    aosp/src/aapt2/link/TableMerger.cpp
    aosp/src/aapt2/link/XmlCompatVersioner.cpp
    aosp/src/aapt2/link/XmlNamespaceRemover.cpp
    aosp/src/aapt2/link/XmlReferenceLinker.cpp
    aosp/src/aapt2/optimize/MultiApkGenerator.cpp
    aosp/src/aapt2/optimize/ResourceDeduper.cpp
    aosp/src/aapt2/optimize/ResourceFilter.cpp
    aosp/src/aapt2/optimize/ResourcePathShortener.cpp
    aosp/src/aapt2/optimize/VersionCollapser.cpp
    aosp/src/aapt2/process/SymbolTable.cpp
    aosp/src/aapt2/split/TableSplitter.cpp
    aosp/src/aapt2/text/Printer.cpp
    aosp/src/aapt2/text/Unicode.cpp
    aosp/src/aapt2/text/Utf8Iterator.cpp
    aosp/src/aapt2/util/BigBuffer.cpp
    aosp/src/aapt2/util/Files.cpp
    aosp/src/aapt2/util/Util.cpp
    aosp/src/aapt2/Debug.cpp
    aosp/src/aapt2/DominatorTree.cpp
    aosp/src/aapt2/java/AnnotationProcessor.cpp
    aosp/src/aapt2/java/ClassDefinition.cpp
    aosp/src/aapt2/java/JavaClassGenerator.cpp
    aosp/src/aapt2/java/ManifestClassGenerator.cpp
    aosp/src/aapt2/java/ProguardRules.cpp
    aosp/src/aapt2/LoadedApk.cpp
    aosp/src/aapt2/Resource.cpp
    aosp/src/aapt2/ResourceParser.cpp
    aosp/src/aapt2/ResourceTable.cpp
    aosp/src/aapt2/ResourceUtils.cpp
    aosp/src/aapt2/ResourceValues.cpp
    aosp/src/aapt2/SdkConstants.cpp
    aosp/src/aapt2/StringPool.cpp
    aosp/src/aapt2/trace/TraceBuffer.cpp
    aosp/src/aapt2/xml/XmlActionExecutor.cpp
    aosp/src/aapt2/xml/XmlDom.cpp
    aosp/src/aapt2/xml/XmlPullParser.cpp
    aosp/src/aapt2/xml/XmlUtil.cpp
    aosp/src/aapt2/ValueTransformer.cpp
    aosp/src/aapt2/Configuration.proto
    aosp/src/aapt2/Resources.proto
    aosp/src/aapt2/ResourcesInternal.proto
    ${AAPT2_PROTO_SRC} ${AAPT2_PROTO_HDRS}
    aapt2_jni.cpp)
target_include_directories(aapt2 PRIVATE
    aosp/src/aapt2
    aosp/src/libidmap2_policies/include
    aosp/src/googletest/googletest/include
    aosp/src/protobuf/src
    aosp/src/fmtlib/include)
target_link_libraries(aapt2 ${android-lib} base utils cutils log androidfw ziparchive incfs protobuf png expat zlib)

if (NOT CMAKE_BUILD_TYPE STREQUAL "Debug")
    add_custom_command(TARGET aapt2 POST_BUILD
            COMMAND ${CMAKE_STRIP} --remove-section=.comment "${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libaapt2.so")
endif()
