package genowa.model;

import genowa.util.IString;
import java.util.ArrayList;
import java.util.List;

/**
 * GenDataModels - Data model classes for GEN_* configuration tables.
 * Matches C++ GenDataModels.hpp, maintaining IString usage.
 */
public class GenDataModels
{
    // ========== Data Model Classes ==========
    
    public static class GenTableType
    {
        public int typeId;
        public IString typeCode;
        public IString typeDescription;
        public IString typeConfig;        // JSON configuration

        public GenTableType()
        {
            this.typeCode = new IString("");
            this.typeDescription = new IString("");
            this.typeConfig = new IString("");
        }
    }

    public static class GenApplication
    {
        public int applicationId;
        public IString applicationCode;
        public IString applicationName;
        public IString applicationConfig; // JSON configuration

        public GenApplication()
        {
            this.applicationCode = new IString("");
            this.applicationName = new IString("");
            this.applicationConfig = new IString("");
        }
    }

    public static class GenLinkageType
    {
        public int linkageTypeId;
        public IString linkageTypeCode;
        public IString linkageTypeName;
        public IString linkageTypeConfig; // JSON configuration

        public GenLinkageType()
        {
            this.linkageTypeCode = new IString("");
            this.linkageTypeName = new IString("");
            this.linkageTypeConfig = new IString("");
        }
    }

    public static class GenTable
    {
        public int tableId;
        public IString tableName;
        public IString tableDescription;
        public int tableTypeId;
        public int applicationId;
        public IString tableConfig;       // JSON configuration

        public GenTable()
        {
            this.tableName = new IString("");
            this.tableDescription = new IString("");
            this.tableConfig = new IString("");
        }
    }

    public static class GenDataType
    {
        public int dataTypeId;
        public IString dataTypeCode;
        public IString dataTypeName;
        public IString dataTypeConfig;    // JSON configuration

        public GenDataType()
        {
            this.dataTypeCode = new IString("");
            this.dataTypeName = new IString("");
            this.dataTypeConfig = new IString("");
        }
    }

    public static class GenField
    {
        public int fieldId;
        public int tableId;
        public IString fieldName;
        public IString fieldDescription;
        public int dataTypeId;
        public int fieldLength;
        public int fieldPrecision;
        public int fieldScale;
        public IString fieldConfig;       // JSON configuration

        public GenField()
        {
            this.fieldName = new IString("");
            this.fieldDescription = new IString("");
            this.fieldConfig = new IString("");
        }
    }

    public static class GenModuleType
    {
        public int moduleTypeId;
        public IString moduleTypeCode;
        public IString moduleTypeName;
        public IString moduleTypeConfig;  // JSON configuration

        public GenModuleType()
        {
            this.moduleTypeCode = new IString("");
            this.moduleTypeName = new IString("");
            this.moduleTypeConfig = new IString("");
        }
    }

    public static class GenAlgorithm
    {
        public int algorithmId;
        public IString algorithmName;
        public IString algorithmDescription;
        public IString algorithmConfig;   // JSON configuration

        public GenAlgorithm()
        {
            this.algorithmName = new IString("");
            this.algorithmDescription = new IString("");
            this.algorithmConfig = new IString("");
        }
    }

    public static class GenSequence
    {
        public int sequenceId;
        public IString sequenceName;
        public IString sequenceDescription;
        public IString sequenceConfig;    // JSON configuration

        public GenSequence()
        {
            this.sequenceName = new IString("");
            this.sequenceDescription = new IString("");
            this.sequenceConfig = new IString("");
        }
    }

    public static class GenRule
    {
        public int ruleId;
        public int tableId;
        public IString ruleName;
        public IString ruleType;
        public IString templateName;
        public IString moduleTypeCode;
        public IString outputFormat;      // 'cobol', 'java', 'cpp'
        public IString ruleConfig;        // JSON configuration

        public GenRule()
        {
            this.ruleName = new IString("");
            this.ruleType = new IString("");
            this.templateName = new IString("");
            this.moduleTypeCode = new IString("");
            this.outputFormat = new IString("");
            this.ruleConfig = new IString("");
        }
    }

    public static class GenTemplate
    {
        public int templateId;
        public IString templateName;
        public IString templateDescription;
        public IString templateContent;
        public IString outputFormat;      // 'cobol', 'java', 'cpp'
        public IString templateConfig;    // JSON configuration

        public GenTemplate()
        {
            this.templateName = new IString("");
            this.templateDescription = new IString("");
            this.templateContent = new IString("");
            this.outputFormat = new IString("");
            this.templateConfig = new IString("");
        }
    }

    // Foundation table structs for configuration-driven system
    public static class GenTableDefinition
    {
        public int tableDefId;
        public IString tableName;
        public IString tableCategory;  // 'GEN_*', 'BUSINESS', 'EXTERNAL'
        public IString tableDescription;
        public boolean isActive;
        public IString tableConfig;    // JSON configuration

        public GenTableDefinition()
        {
            this.tableDefId = 0;
            this.tableName = new IString("");
            this.tableCategory = new IString("");
            this.tableDescription = new IString("");
            this.isActive = true;
            this.tableConfig = new IString("");
        }
    }

    public static class GenColumnDefinition
    {
        public int columnDefId;
        public int tableDefId;
        public IString columnName;
        public IString columnType;
        public int columnLength;
        public boolean isNullable;
        public boolean isPrimaryKey;
        public boolean isIndexed;
        public boolean isSearchable;
        public IString columnConfig;   // JSON configuration

        public GenColumnDefinition()
        {
            this.columnDefId = 0;
            this.tableDefId = 0;
            this.columnName = new IString("");
            this.columnType = new IString("");
            this.columnLength = 0;
            this.isNullable = false;
            this.isPrimaryKey = false;
            this.isIndexed = false;
            this.isSearchable = false;
            this.columnConfig = new IString("");
        }
    }

    public static class GenQueryPattern
    {
        public int patternId;
        public int tableDefId;
        public IString patternName;
        public IString patternDescription;
        public IString whereClause;
        public int parameterCount;
        public boolean isActive;
        public IString patternConfig;  // JSON configuration

        public GenQueryPattern()
        {
            this.patternId = 0;
            this.tableDefId = 0;
            this.patternName = new IString("");
            this.patternDescription = new IString("");
            this.whereClause = new IString("");
            this.parameterCount = 0;
            this.isActive = true;
            this.patternConfig = new IString("");
        }
    }

    // ========== Collection Classes ==========

    public static class GenTableTypeCollection
    {
        private List<GenTableType> items = new ArrayList<>();

        public void addItem(GenTableType item)
        {
            items.add(item);
        }

        public List<GenTableType> getItems()
        {
            return new ArrayList<>(items);
        }

        public GenTableType findById(int typeId)
        {
            for (GenTableType item : items)
            {
                if (item.typeId == typeId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenTableType findByCode(IString typeCode)
        {
            if (typeCode == null)
            {
                return null;
            }
            for (GenTableType item : items)
            {
                if (item.typeCode != null && item.typeCode.equals(typeCode))
                {
                    return item;
                }
            }
            return null;
        }
    }

    public static class GenApplicationCollection
    {
        private List<GenApplication> items = new ArrayList<>();

        public void addItem(GenApplication item)
        {
            items.add(item);
        }

        public List<GenApplication> getItems()
        {
            return new ArrayList<>(items);
        }

        public GenApplication findById(int applicationId)
        {
            for (GenApplication item : items)
            {
                if (item.applicationId == applicationId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenApplication findByCode(IString applicationCode)
        {
            if (applicationCode == null)
            {
                return null;
            }
            for (GenApplication item : items)
            {
                if (item.applicationCode != null && item.applicationCode.equals(applicationCode))
                {
                    return item;
                }
            }
            return null;
        }
    }

    public static class GenLinkageTypeCollection
    {
        private List<GenLinkageType> items = new ArrayList<>();

        public void addItem(GenLinkageType item)
        {
            items.add(item);
        }

        public List<GenLinkageType> getItems()
        {
            return new ArrayList<>(items);
        }

        public GenLinkageType findById(int linkageTypeId)
        {
            for (GenLinkageType item : items)
            {
                if (item.linkageTypeId == linkageTypeId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenLinkageType findByCode(IString linkageTypeCode)
        {
            if (linkageTypeCode == null)
            {
                return null;
            }
            for (GenLinkageType item : items)
            {
                if (item.linkageTypeCode != null && item.linkageTypeCode.equals(linkageTypeCode))
                {
                    return item;
                }
            }
            return null;
        }
    }

    public static class GenTableCollection
    {
        private List<GenTable> items = new ArrayList<>();

        public void addItem(GenTable item)
        {
            items.add(item);
        }

        public List<GenTable> getItems()
        {
            return new ArrayList<>(items);
        }

        public GenTable findById(int tableId)
        {
            for (GenTable item : items)
            {
                if (item.tableId == tableId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenTable findByName(IString tableName)
        {
            if (tableName == null)
            {
                return null;
            }
            for (GenTable item : items)
            {
                if (item.tableName != null && item.tableName.equals(tableName))
                {
                    return item;
                }
            }
            return null;
        }

        public List<GenTable> findByApplication(int applicationId)
        {
            List<GenTable> result = new ArrayList<>();
            for (GenTable item : items)
            {
                if (item.applicationId == applicationId)
                {
                    result.add(item);
                }
            }
            return result;
        }

        public List<GenTable> findByType(int tableTypeId)
        {
            List<GenTable> result = new ArrayList<>();
            for (GenTable item : items)
            {
                if (item.tableTypeId == tableTypeId)
                {
                    result.add(item);
                }
            }
            return result;
        }
    }

    public static class GenDataTypeCollection
    {
        private List<GenDataType> items = new ArrayList<>();

        public void addItem(GenDataType item)
        {
            items.add(item);
        }

        public List<GenDataType> getItems()
        {
            return new ArrayList<>(items);
        }

        public GenDataType findById(int dataTypeId)
        {
            for (GenDataType item : items)
            {
                if (item.dataTypeId == dataTypeId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenDataType findByCode(IString dataTypeCode)
        {
            if (dataTypeCode == null)
            {
                return null;
            }
            for (GenDataType item : items)
            {
                if (item.dataTypeCode != null && item.dataTypeCode.equals(dataTypeCode))
                {
                    return item;
                }
            }
            return null;
        }
    }

    public static class GenFieldCollection
    {
        private List<GenField> items = new ArrayList<>();

        public void addItem(GenField item)
        {
            items.add(item);
        }

        public List<GenField> getItems()
        {
            return new ArrayList<>(items);
        }

        public GenField findById(int fieldId)
        {
            for (GenField item : items)
            {
                if (item.fieldId == fieldId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenField findByName(IString fieldName)
        {
            if (fieldName == null)
            {
                return null;
            }
            for (GenField item : items)
            {
                if (item.fieldName != null && item.fieldName.equals(fieldName))
                {
                    return item;
                }
            }
            return null;
        }

        public List<GenField> findByTable(int tableId)
        {
            List<GenField> result = new ArrayList<>();
            for (GenField item : items)
            {
                if (item.tableId == tableId)
                {
                    result.add(item);
                }
            }
            return result;
        }
    }

    public static class GenModuleTypeCollection
    {
        private List<GenModuleType> items = new ArrayList<>();

        public void addItem(GenModuleType item)
        {
            items.add(item);
        }

        public List<GenModuleType> getItems()
        {
            return new ArrayList<>(items);
        }

        public GenModuleType findById(int moduleTypeId)
        {
            for (GenModuleType item : items)
            {
                if (item.moduleTypeId == moduleTypeId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenModuleType findByCode(IString moduleTypeCode)
        {
            if (moduleTypeCode == null)
            {
                return null;
            }
            for (GenModuleType item : items)
            {
                if (item.moduleTypeCode != null && item.moduleTypeCode.equals(moduleTypeCode))
                {
                    return item;
                }
            }
            return null;
        }
    }

    public static class GenAlgorithmCollection
    {
        private List<GenAlgorithm> items = new ArrayList<>();

        public void addItem(GenAlgorithm item)
        {
            items.add(item);
        }

        public List<GenAlgorithm> getItems()
        {
            return new ArrayList<>(items);
        }

        public GenAlgorithm findById(int algorithmId)
        {
            for (GenAlgorithm item : items)
            {
                if (item.algorithmId == algorithmId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenAlgorithm findByName(IString algorithmName)
        {
            if (algorithmName == null)
            {
                return null;
            }
            for (GenAlgorithm item : items)
            {
                if (item.algorithmName != null && item.algorithmName.equals(algorithmName))
                {
                    return item;
                }
            }
            return null;
        }
    }

    public static class GenSequenceCollection
    {
        private List<GenSequence> items = new ArrayList<>();

        public void addItem(GenSequence item)
        {
            items.add(item);
        }

        public List<GenSequence> getItems()
        {
            return new ArrayList<>(items);
        }

        public GenSequence findById(int sequenceId)
        {
            for (GenSequence item : items)
            {
                if (item.sequenceId == sequenceId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenSequence findByName(IString sequenceName)
        {
            if (sequenceName == null)
            {
                return null;
            }
            for (GenSequence item : items)
            {
                if (item.sequenceName != null && item.sequenceName.equals(sequenceName))
                {
                    return item;
                }
            }
            return null;
        }
    }

    public static class GenRuleCollection
    {
        private List<GenRule> items = new ArrayList<>();

        public void addItem(GenRule item)
        {
            items.add(item);
        }

        public List<GenRule> getItems()
        {
            return new ArrayList<>(items);
        }

        public List<GenRule> findByTable(int tableId)
        {
            List<GenRule> result = new ArrayList<>();
            for (GenRule item : items)
            {
                if (item.tableId == tableId)
                {
                    result.add(item);
                }
            }
            return result;
        }

        public GenRule findById(int ruleId)
        {
            for (GenRule item : items)
            {
                if (item.ruleId == ruleId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenRule findByName(IString ruleName)
        {
            if (ruleName == null)
            {
                return null;
            }
            for (GenRule item : items)
            {
                if (item.ruleName != null && item.ruleName.equals(ruleName))
                {
                    return item;
                }
            }
            return null;
        }
    }

    public static class GenTemplateCollection
    {
        private List<GenTemplate> items = new ArrayList<>();

        public void addItem(GenTemplate item)
        {
            items.add(item);
        }

        public List<GenTemplate> getItems()
        {
            return new ArrayList<>(items);
        }

        public GenTemplate findById(int templateId)
        {
            for (GenTemplate item : items)
            {
                if (item.templateId == templateId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenTemplate findByName(IString templateName)
        {
            if (templateName == null)
            {
                return null;
            }
            for (GenTemplate item : items)
            {
                if (item.templateName != null && item.templateName.equals(templateName))
                {
                    return item;
                }
            }
            return null;
        }

        public List<GenTemplate> findByOutputFormat(IString outputFormat)
        {
            List<GenTemplate> result = new ArrayList<>();
            if (outputFormat == null)
            {
                return result;
            }
            for (GenTemplate item : items)
            {
                if (item.outputFormat != null && item.outputFormat.equals(outputFormat))
                {
                    result.add(item);
                }
            }
            return result;
        }
    }

    // Foundation table collections
    public static class GenTableDefinitionCollection
    {
        private List<GenTableDefinition> items = new ArrayList<>();

        public void addItem(GenTableDefinition item)
        {
            items.add(item);
        }

        public GenTableDefinition findById(int id)
        {
            for (GenTableDefinition item : items)
            {
                if (item.tableDefId == id)
                {
                    return item;
                }
            }
            return null;
        }

        public GenTableDefinition findByName(IString name)
        {
            if (name == null)
            {
                return null;
            }
            for (GenTableDefinition item : items)
            {
                if (item.tableName != null && item.tableName.equals(name))
                {
                    return item;
                }
            }
            return null;
        }

        public GenTableDefinition findByCategory(IString category)
        {
            if (category == null)
            {
                return null;
            }
            for (GenTableDefinition item : items)
            {
                if (item.tableCategory != null && item.tableCategory.equals(category))
                {
                    return item;
                }
            }
            return null;
        }

        public List<GenTableDefinition> getAllItems()
        {
            return new ArrayList<>(items);
        }

        public int size()
        {
            return items.size();
        }

        public void clear()
        {
            items.clear();
        }
    }

    public static class GenColumnDefinitionCollection
    {
        private List<GenColumnDefinition> items = new ArrayList<>();

        public void addItem(GenColumnDefinition item)
        {
            items.add(item);
        }

        public GenColumnDefinition findById(int id)
        {
            for (GenColumnDefinition item : items)
            {
                if (item.columnDefId == id)
                {
                    return item;
                }
            }
            return null;
        }

        public GenColumnDefinition findByTable(int tableDefId)
        {
            for (GenColumnDefinition item : items)
            {
                if (item.tableDefId == tableDefId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenColumnDefinition findByName(IString name)
        {
            if (name == null)
            {
                return null;
            }
            for (GenColumnDefinition item : items)
            {
                if (item.columnName != null && item.columnName.equals(name))
                {
                    return item;
                }
            }
            return null;
        }

        public List<GenColumnDefinition> getAllItems()
        {
            return new ArrayList<>(items);
        }

        public int size()
        {
            return items.size();
        }

        public void clear()
        {
            items.clear();
        }
    }

    public static class GenQueryPatternCollection
    {
        private List<GenQueryPattern> items = new ArrayList<>();

        public void addItem(GenQueryPattern item)
        {
            items.add(item);
        }

        public GenQueryPattern findById(int id)
        {
            for (GenQueryPattern item : items)
            {
                if (item.patternId == id)
                {
                    return item;
                }
            }
            return null;
        }

        public GenQueryPattern findByTable(int tableDefId)
        {
            for (GenQueryPattern item : items)
            {
                if (item.tableDefId == tableDefId)
                {
                    return item;
                }
            }
            return null;
        }

        public GenQueryPattern findByName(IString name)
        {
            if (name == null)
            {
                return null;
            }
            for (GenQueryPattern item : items)
            {
                if (item.patternName != null && item.patternName.equals(name))
                {
                    return item;
                }
            }
            return null;
        }

        public List<GenQueryPattern> getAllItems()
        {
            return new ArrayList<>(items);
        }

        public int size()
        {
            return items.size();
        }

        public void clear()
        {
            items.clear();
        }
    }
}

