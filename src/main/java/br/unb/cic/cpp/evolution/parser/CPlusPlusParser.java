package br.unb.cic.cpp.evolution.parser;

import lombok.NoArgsConstructor;
import lombok.val;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.core.runtime.CoreException;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class CPPParser {

    public IASTTranslationUnit parse(String content) throws CoreException {
        val fc = FileContent.create("", content.toCharArray());
        val macroDefinitions = new HashMap<String, String>();
        val includeSearchPaths = new String[0];
        val si = new ScannerInfo(macroDefinitions, includeSearchPaths);
        val icfp = IncludeFileContentProvider.getEmptyFilesProvider();
        val options = ILanguage.OPTION_IS_SOURCE_UNIT;

        IIndex idx = null;
        IParserLogService log = new DefaultLogService();

        return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, icfp, idx, options, log);
    }
}
