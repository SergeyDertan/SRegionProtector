package Sergey_Dertan.SRegionProtector.Utils;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.PersistenceUnitMetaData;

import javax.jdo.JDOException;

public class J extends JDOPersistenceManagerFactory {

    public J(PersistenceUnitMetaData pumd) {
        super(pumd, null);
    }

    @Override
    protected void initialiseMetaData(PersistenceUnitMetaData pumd) {
        this.nucleusContext.getMetaDataManager().setAllowXML(this.getConfiguration().getBooleanProperty("datanucleus.metadata.allowXML"));
        this.nucleusContext.getMetaDataManager().setAllowAnnotations(this.getConfiguration().getBooleanProperty("datanucleus.metadata.allowAnnotations"));
        this.nucleusContext.getMetaDataManager().setValidate(this.getConfiguration().getBooleanProperty("datanucleus.metadata.xml.validate"));
        this.nucleusContext.getMetaDataManager().setDefaultNullable(this.getConfiguration().getBooleanProperty("datanucleus.metadata.defaultNullable"));
        if (pumd != null) {
            try {
                this.nucleusContext.getMetaDataManager().loadPersistenceUnit(pumd, SRegionProtectorMain.class.getClassLoader());
                if (pumd.getValidationMode() != null) {
                    this.getConfiguration().setProperty("datanucleus.validation.mode", pumd.getValidationMode());
                }
            } catch (NucleusException var3) {
                throw new JDOException(var3.getMessage(), var3);
            }
        }

        boolean allowMetadataLoad = this.nucleusContext.getConfiguration().getBooleanProperty("datanucleus.metadata.allowLoadAtRuntime");
        if (!allowMetadataLoad) {
            this.nucleusContext.getMetaDataManager().setAllowMetaDataLoad(false);
        }
    }
}
