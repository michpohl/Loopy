//
// Created by michael on 01.11.20.
//

#ifndef LOOPY_CONVERTER_H
#define LOOPY_CONVERTER_H


class Converter {
public:

    explicit  Converter();
    bool setDestinationFolder(const char *folderName);
    bool convertFolder();
    bool convertSingleFile(const char *filePath, const char *fileName);

private:
    const char *mFolder;
    bool doConversion(const std::string& fullPath, const std::string& name);
    std::string wav = ".wav";
    std::string mp3 = ".mp3";
    std::string ogg = ".ogg";
    std::string pcm = ".pcm";

    static inline bool endsWith(std::string const & value, std::string const & ending)
    {
        if (ending.size() > value.size()) return false;
        return std::equal(ending.rbegin(), ending.rend(), value.rbegin());
    }
};

#endif //LOOPY_CONVERTER_H
