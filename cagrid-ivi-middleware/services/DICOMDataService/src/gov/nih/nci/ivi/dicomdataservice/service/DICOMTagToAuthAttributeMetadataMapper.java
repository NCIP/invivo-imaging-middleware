/**
 * Copyright 2009 Emory University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated  * documentation files (the 
 * "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions: The above copyright notice and this permission
 * notice shall be included in all copies or substantial portions of the
 * Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package gov.nih.nci.ivi.dicomdataservice.service;

import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.TagFromName;

//import edu.emory.cci.security.auth.AttributeMetadata;
//import edu.emory.cci.security.auth.AttributeMetadataDAO;

/**
 * This class is responsible for mapping DICOM tags to equivalent Authorization
 * attribute metadata. It will create metadata in the authorization database if
 * it does not exist.
 * 
 * @author Mark Grand
 */
class DICOMTagToAuthAttributeMetadataMapper {
    /**
     * Map from DICOM attribute tags to authorization database metadata. The
     * authorization database metadata is used to put attributes corresponding
     * to DICOM tags for DICOM data in the authorization database.
     */
//    private final static Map<AttributeTag, AttributeMetadata> attributeMetadataMap = new HashMap<AttributeTag, AttributeMetadata>();

    /**
     * Map from DICOM attribute tags to names. The names are used if metadata
     * for a DICOM tag needs to be created in the database.
     */
//    private final static Map<AttributeTag, String> tagNameMap = new HashMap<AttributeTag, String>();

    // Populate the maps. The map to attribute metadata has all nulls for
    // values. The actual AttributeValue objects will be added when they are
    // first needed.
    static {
        ensureInAttributeMetadata(TagFromName.AbsoluteChannelDisplayScale, "AbsoluteChannelDisplayScale");
        ensureInAttributeMetadata(TagFromName.AbstractPriorCodeSequence, "AbstractPriorCodeSequence");
        ensureInAttributeMetadata(TagFromName.AbstractPriorValue, "AbstractPriorValue");
        ensureInAttributeMetadata(TagFromName.AccessionNumber, "AccessionNumber");
        ensureInAttributeMetadata(TagFromName.AccessoryCode, "AccessoryCode");
        ensureInAttributeMetadata(TagFromName.AcquiredImageAreaDoseProduct, "AcquiredImageAreaDoseProduct");
        ensureInAttributeMetadata(TagFromName.AcquisitionComments, "AcquisitionComments");
        ensureInAttributeMetadata(TagFromName.AcquisitionContextDescription, "AcquisitionContextDescription");
        ensureInAttributeMetadata(TagFromName.AcquisitionContextSequence, "AcquisitionContextSequence");
        ensureInAttributeMetadata(TagFromName.AcquisitionContrast, "AcquisitionContrast");
        ensureInAttributeMetadata(TagFromName.AcquisitionDate, "AcquisitionDate");
        ensureInAttributeMetadata(TagFromName.AcquisitionDateTime, "AcquisitionDateTime");
        ensureInAttributeMetadata(TagFromName.AcquisitionDeviceProcessingCode, "AcquisitionDeviceProcessingCode");
        ensureInAttributeMetadata(TagFromName.AcquisitionDeviceProcessingDescription,
                "AcquisitionDeviceProcessingDescription");
        ensureInAttributeMetadata(TagFromName.AcquisitionDeviceTypeCodeSequence, "AcquisitionDeviceTypeCodeSequence");
        ensureInAttributeMetadata(TagFromName.AcquisitionDuration, "AcquisitionDuration");
        ensureInAttributeMetadata(TagFromName.AcquisitionIndex, "AcquisitionIndex");
        ensureInAttributeMetadata(TagFromName.AcquisitionMatrix, "AcquisitionMatrix");
        ensureInAttributeMetadata(TagFromName.AcquisitionNumber, "AcquisitionNumber");
        ensureInAttributeMetadata(TagFromName.AcquisitionProtocolDescription, "AcquisitionProtocolDescription");
        ensureInAttributeMetadata(TagFromName.AcquisitionProtocolName, "AcquisitionProtocolName");
        ensureInAttributeMetadata(TagFromName.AcquisitionsInSeries, "AcquisitionsInSeries");
        ensureInAttributeMetadata(TagFromName.AcquisitionsInStudy, "AcquisitionsInStudy");
        ensureInAttributeMetadata(TagFromName.AcquisitionStartCondition, "AcquisitionStartCondition");
        ensureInAttributeMetadata(TagFromName.AcquisitionStartConditionData, "AcquisitionStartConditionData");
        ensureInAttributeMetadata(TagFromName.AcquisitionTerminationCondition, "AcquisitionTerminationCondition");
        ensureInAttributeMetadata(TagFromName.AcquisitionTerminationConditionData,
                "AcquisitionTerminationConditionData");
        ensureInAttributeMetadata(TagFromName.AcquisitionTime, "AcquisitionTime");
        ensureInAttributeMetadata(TagFromName.AcquisitionTimeSynchronized, "AcquisitionTimeSynchronized");
        ensureInAttributeMetadata(TagFromName.AcquisitionType, "AcquisitionType");
        ensureInAttributeMetadata(TagFromName.AcrossScanSpatialResolution, "AcrossScanSpatialResolution");
        ensureInAttributeMetadata(TagFromName.ActionTypeID, "ActionTypeID");
        ensureInAttributeMetadata(TagFromName.ActiveSourceDiameter, "ActiveSourceDiameter");
        ensureInAttributeMetadata(TagFromName.ActiveSourceLength, "ActiveSourceLength");
        ensureInAttributeMetadata(TagFromName.ActualCardiacTriggerDelayTime, "ActualCardiacTriggerDelayTime");
        ensureInAttributeMetadata(TagFromName.ActualFrameDuration, "ActualFrameDuration");
        ensureInAttributeMetadata(TagFromName.ActualHumanPerformersSequence, "ActualHumanPerformersSequence");
        ensureInAttributeMetadata(TagFromName.ActualRespiratoryTriggerDelayTime, "ActualRespiratoryTriggerDelayTime");
        ensureInAttributeMetadata(TagFromName.AdaptiveMapFormat, "AdaptiveMapFormat");
        ensureInAttributeMetadata(TagFromName.AddGrayScale, "AddGrayScale");
        ensureInAttributeMetadata(TagFromName.AdditionalDrugSequence, "AdditionalDrugSequence");
        ensureInAttributeMetadata(TagFromName.AdditionalPatientHistory, "AdditionalPatientHistory");
        ensureInAttributeMetadata(TagFromName.AddressTrial, "AddressTrial");
        ensureInAttributeMetadata(TagFromName.AdministrationRouteCodeSequence, "AdministrationRouteCodeSequence");
        ensureInAttributeMetadata(TagFromName.AdmissionID, "AdmissionID");
        ensureInAttributeMetadata(TagFromName.AdmittingDate, "AdmittingDate");
        ensureInAttributeMetadata(TagFromName.AdmittingDiagnosesCodeSequence, "AdmittingDiagnosesCodeSequence");
        ensureInAttributeMetadata(TagFromName.AdmittingDiagnosesDescription, "AdmittingDiagnosesDescription");
        ensureInAttributeMetadata(TagFromName.AdmittingTime, "AdmittingTime");
        ensureInAttributeMetadata(TagFromName.AffectedSOPClassUID, "AffectedSOPClassUID");
        ensureInAttributeMetadata(TagFromName.AffectedSOPInstanceUID, "AffectedSOPInstanceUID");
        // ensureInAttributeMetadata(TagFromName.AlgorithmCodeSequenceTrial,
        // "AlgorithmCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.AlgorithmDescription, "AlgorithmDescription");
        // ensureInAttributeMetadata(TagFromName.AlgorithmDescriptionTrial,
        // "AlgorithmDescriptionTrial");
        ensureInAttributeMetadata(TagFromName.AlgorithmType, "AlgorithmType");
        ensureInAttributeMetadata(TagFromName.Allergies, "Allergies");
        ensureInAttributeMetadata(TagFromName.AllowLossyCompression, "AllowLossyCompression");
        ensureInAttributeMetadata(TagFromName.AllowMediaSplitting, "AllowMediaSplitting");
        ensureInAttributeMetadata(TagFromName.AlongScanSpatialResolution, "AlongScanSpatialResolution");
        ensureInAttributeMetadata(TagFromName.AlternateRepresentationSequence, "AlternateRepresentationSequence");
        ensureInAttributeMetadata(TagFromName.AnatomicApproachDirectionCodeSequenceTrial,
                "AnatomicApproachDirectionCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.AnatomicLocationOfExaminingInstrumentCodeSequenceTrial,
                "AnatomicLocationOfExaminingInstrumentCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.AnatomicLocationOfExaminingInstrumentDescriptionTrial,
                "AnatomicLocationOfExaminingInstrumentDescriptionTrial");
        ensureInAttributeMetadata(TagFromName.AnatomicPerspectiveCodeSequenceTrial,
                "AnatomicPerspectiveCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.AnatomicPerspectiveDescriptionTrial,
                "AnatomicPerspectiveDescriptionTrial");
        ensureInAttributeMetadata(TagFromName.AnatomicPortalOfEntranceCodeSequenceTrial,
                "AnatomicPortalOfEntranceCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.AnatomicRegionModifierSequence, "AnatomicRegionModifierSequence");
        ensureInAttributeMetadata(TagFromName.AnatomicRegionSequence, "AnatomicRegionSequence");
        ensureInAttributeMetadata(TagFromName.AnatomicStructure, "AnatomicStructure");
        ensureInAttributeMetadata(TagFromName.AnatomicStructureSpaceOrRegionCodeSequenceTrial,
                "AnatomicStructureSpaceOrRegionCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.AnatomicStructureSpaceOrRegionModifierCodeSequenceTrial,
                "AnatomicStructureSpaceOrRegionModifierCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.AnatomicStructureSpaceOrRegionSequence,
                "AnatomicStructureSpaceOrRegionSequence");
        ensureInAttributeMetadata(TagFromName.AnchorPoint, "AnchorPoint");
        ensureInAttributeMetadata(TagFromName.AnchorPointAnnotationUnits, "AnchorPointAnnotationUnits");
        ensureInAttributeMetadata(TagFromName.AnchorPointVisibility, "AnchorPointVisibility");
        ensureInAttributeMetadata(TagFromName.AngioFlag, "AngioFlag");
        ensureInAttributeMetadata(TagFromName.AngleNumber, "AngleNumber");
        ensureInAttributeMetadata(TagFromName.AngularPosition, "AngularPosition");
        ensureInAttributeMetadata(TagFromName.AngularStep, "AngularStep");
        ensureInAttributeMetadata(TagFromName.AngularViewVector, "AngularViewVector");
        ensureInAttributeMetadata(TagFromName.AnnotationContentSequence, "AnnotationContentSequence");
        ensureInAttributeMetadata(TagFromName.AnnotationDisplayFormatID, "AnnotationDisplayFormatID");
        ensureInAttributeMetadata(TagFromName.AnnotationFlag, "AnnotationFlag");
        ensureInAttributeMetadata(TagFromName.AnnotationGroupNumber, "AnnotationGroupNumber");
        ensureInAttributeMetadata(TagFromName.AnnotationPosition, "AnnotationPosition");
        ensureInAttributeMetadata(TagFromName.AnodeTargetMaterial, "AnodeTargetMaterial");
        ensureInAttributeMetadata(TagFromName.ApplicableFrameRange, "ApplicableFrameRange");
        ensureInAttributeMetadata(TagFromName.ApplicableSafetyStandardAgency, "ApplicableSafetyStandardAgency");
        ensureInAttributeMetadata(TagFromName.ApplicableSafetyStandardDescription,
                "ApplicableSafetyStandardDescription");
        ensureInAttributeMetadata(TagFromName.ApplicationManufacturer, "ApplicationManufacturer");
        ensureInAttributeMetadata(TagFromName.ApplicationMaximumRepaintTime, "ApplicationMaximumRepaintTime");
        ensureInAttributeMetadata(TagFromName.ApplicationName, "ApplicationName");
        ensureInAttributeMetadata(TagFromName.ApplicationSetupCheck, "ApplicationSetupCheck");
        ensureInAttributeMetadata(TagFromName.ApplicationSetupManufacturer, "ApplicationSetupManufacturer");
        ensureInAttributeMetadata(TagFromName.ApplicationSetupName, "ApplicationSetupName");
        ensureInAttributeMetadata(TagFromName.ApplicationSetupNumber, "ApplicationSetupNumber");
        ensureInAttributeMetadata(TagFromName.ApplicationSetupSequence, "ApplicationSetupSequence");
        ensureInAttributeMetadata(TagFromName.ApplicationSetupType, "ApplicationSetupType");
        ensureInAttributeMetadata(TagFromName.ApplicationVersion, "ApplicationVersion");
        ensureInAttributeMetadata(TagFromName.ApplicatorDescription, "ApplicatorDescription");
        ensureInAttributeMetadata(TagFromName.ApplicatorID, "ApplicatorID");
        ensureInAttributeMetadata(TagFromName.ApplicatorSequence, "ApplicatorSequence");
        ensureInAttributeMetadata(TagFromName.ApplicatorType, "ApplicatorType");
        ensureInAttributeMetadata(TagFromName.ApprovalStatus, "ApprovalStatus");
        ensureInAttributeMetadata(TagFromName.ApprovalStatusDateTime, "ApprovalStatusDateTime");
        ensureInAttributeMetadata(TagFromName.ApprovalStatusFurtherDescription, "ApprovalStatusFurtherDescription");
        ensureInAttributeMetadata(TagFromName.Arbitrary, "Arbitrary");
        ensureInAttributeMetadata(TagFromName.ArchiveRequested, "ArchiveRequested");
        // ensureInAttributeMetadata(TagFromName.AssigningAgencyOrDepartmentCodeSequence,
        // "AssigningAgencyOrDepartmentCodeSequence");
        // ensureInAttributeMetadata(TagFromName.AssigningFacilitySequence,
        // "AssigningFacilitySequence");
        // ensureInAttributeMetadata(TagFromName.AssigningJurisdictionCodeSequence,
        // "AssigningJurisdictionCodeSequence");
        ensureInAttributeMetadata(TagFromName.AttachedContours, "AttachedContours");
        ensureInAttributeMetadata(TagFromName.AttenuationCorrectionMethod, "AttenuationCorrectionMethod");
        ensureInAttributeMetadata(TagFromName.AttributeIdentifierList, "AttributeIdentifierList");
        ensureInAttributeMetadata(TagFromName.AttributeModificationDateTime, "AttributeModificationDateTime");
        ensureInAttributeMetadata(TagFromName.AudioComments, "AudioComments");
        ensureInAttributeMetadata(TagFromName.AudioSampleData, "AudioSampleData");
        ensureInAttributeMetadata(TagFromName.AudioSampleFormat, "AudioSampleFormat");
        ensureInAttributeMetadata(TagFromName.AudioType, "AudioType");
        ensureInAttributeMetadata(TagFromName.AuthorizationEquipmentCertificationNumber,
                "AuthorizationEquipmentCertificationNumber");
        ensureInAttributeMetadata(TagFromName.AuthorObserverSequence, "AuthorObserverSequence");
        ensureInAttributeMetadata(TagFromName.AveragePulseWidth, "AveragePulseWidth");
        ensureInAttributeMetadata(TagFromName.AxialAcceptance, "AxialAcceptance");
        ensureInAttributeMetadata(TagFromName.AxialLengthOfEye, "AxialLengthOfEye");
        ensureInAttributeMetadata(TagFromName.AxialMash, "AxialMash");
        ensureInAttributeMetadata(TagFromName.AxisLabels, "AxisLabels");
        ensureInAttributeMetadata(TagFromName.AxisUnits, "AxisUnits");
        ensureInAttributeMetadata(TagFromName.BarcodeSymbology, "BarcodeSymbology");
        ensureInAttributeMetadata(TagFromName.BarcodeValue, "BarcodeValue");
        ensureInAttributeMetadata(TagFromName.BaselineCorrection, "BaselineCorrection");
        ensureInAttributeMetadata(TagFromName.BasicColorImageSequence, "BasicColorImageSequence");
        ensureInAttributeMetadata(TagFromName.BasicGrayscaleImageSequence, "BasicGrayscaleImageSequence");
        ensureInAttributeMetadata(TagFromName.BeamAngle, "BeamAngle");
        ensureInAttributeMetadata(TagFromName.BeamCurrentModulationID, "BeamCurrentModulationID");
        ensureInAttributeMetadata(TagFromName.BeamDescription, "BeamDescription");
        ensureInAttributeMetadata(TagFromName.BeamDose, "BeamDose");
        ensureInAttributeMetadata(TagFromName.BeamDosePointDepth, "BeamDosePointDepth");
        ensureInAttributeMetadata(TagFromName.BeamDosePointEquivalentDepth, "BeamDosePointEquivalentDepth");
        ensureInAttributeMetadata(TagFromName.BeamDosePointSSD, "BeamDosePointSSD");
        ensureInAttributeMetadata(TagFromName.BeamDoseSpecificationPoint, "BeamDoseSpecificationPoint");
        ensureInAttributeMetadata(TagFromName.BeamLimitingDeviceAngle, "BeamLimitingDeviceAngle");
        ensureInAttributeMetadata(TagFromName.BeamLimitingDeviceAngleTolerance, "BeamLimitingDeviceAngleTolerance");
        ensureInAttributeMetadata(TagFromName.BeamLimitingDeviceLeafPairsSequence,
                "BeamLimitingDeviceLeafPairsSequence");
        ensureInAttributeMetadata(TagFromName.BeamLimitingDevicePositionSequence, "BeamLimitingDevicePositionSequence");
        ensureInAttributeMetadata(TagFromName.BeamLimitingDevicePositionTolerance,
                "BeamLimitingDevicePositionTolerance");
        ensureInAttributeMetadata(TagFromName.BeamLimitingDeviceRotationDirection,
                "BeamLimitingDeviceRotationDirection");
        ensureInAttributeMetadata(TagFromName.BeamLimitingDeviceSequence, "BeamLimitingDeviceSequence");
        ensureInAttributeMetadata(TagFromName.BeamLimitingDeviceToleranceSequence,
                "BeamLimitingDeviceToleranceSequence");
        ensureInAttributeMetadata(TagFromName.BeamMeterset, "BeamMeterset");
        ensureInAttributeMetadata(TagFromName.BeamName, "BeamName");
        ensureInAttributeMetadata(TagFromName.BeamNumber, "BeamNumber");
        ensureInAttributeMetadata(TagFromName.BeamSequence, "BeamSequence");
        ensureInAttributeMetadata(TagFromName.BeamStopperPosition, "BeamStopperPosition");
        ensureInAttributeMetadata(TagFromName.BeamType, "BeamType");
        ensureInAttributeMetadata(TagFromName.BeatRejectionFlag, "BeatRejectionFlag");
        // ensureInAttributeMetadata(TagFromName.BibliographicCitationTrial,
        // "BibliographicCitationTrial");
        ensureInAttributeMetadata(TagFromName.BillingItemSequence, "BillingItemSequence");
        ensureInAttributeMetadata(TagFromName.BillingProcedureStepSequence, "BillingProcedureStepSequence");
        ensureInAttributeMetadata(TagFromName.BillingSuppliesAndDevicesSequence, "BillingSuppliesAndDevicesSequence");
        ensureInAttributeMetadata(TagFromName.BiplaneAcquisitionSequence, "BiplaneAcquisitionSequence");
        ensureInAttributeMetadata(TagFromName.BitsAllocated, "BitsAllocated");
        ensureInAttributeMetadata(TagFromName.BitsForCodeWord, "BitsForCodeWord");
        ensureInAttributeMetadata(TagFromName.BitsGrouped, "BitsGrouped");
        ensureInAttributeMetadata(TagFromName.BitsStored, "BitsStored");
        ensureInAttributeMetadata(TagFromName.BlendingOperationType, "BlendingOperationType");
        ensureInAttributeMetadata(TagFromName.BlendingPosition, "BlendingPosition");
        ensureInAttributeMetadata(TagFromName.BlendingSequence, "BlendingSequence");
        ensureInAttributeMetadata(TagFromName.BlockColumns, "BlockColumns");
        ensureInAttributeMetadata(TagFromName.BlockData, "BlockData");
        ensureInAttributeMetadata(TagFromName.BlockDivergence, "BlockDivergence");
        ensureInAttributeMetadata(TagFromName.BlockedPixels, "BlockedPixels");
        ensureInAttributeMetadata(TagFromName.BlockMountingPosition, "BlockMountingPosition");
        ensureInAttributeMetadata(TagFromName.BlockName, "BlockName");
        ensureInAttributeMetadata(TagFromName.BlockNumber, "BlockNumber");
        ensureInAttributeMetadata(TagFromName.BlockNumberOfPoints, "BlockNumberOfPoints");
        ensureInAttributeMetadata(TagFromName.BlockRows, "BlockRows");
        ensureInAttributeMetadata(TagFromName.BlockSequence, "BlockSequence");
        ensureInAttributeMetadata(TagFromName.BlockThickness, "BlockThickness");
        ensureInAttributeMetadata(TagFromName.BlockTransmission, "BlockTransmission");
        ensureInAttributeMetadata(TagFromName.BlockTrayID, "BlockTrayID");
        ensureInAttributeMetadata(TagFromName.BlockType, "BlockType");
        ensureInAttributeMetadata(TagFromName.BloodSignalNulling, "BloodSignalNulling");
        ensureInAttributeMetadata(TagFromName.BluePaletteColorLookupTableData, "BluePaletteColorLookupTableData");
        ensureInAttributeMetadata(TagFromName.BluePaletteColorLookupTableDescriptor,
                "BluePaletteColorLookupTableDescriptor");
        ensureInAttributeMetadata(TagFromName.BodyPartExamined, "BodyPartExamined");
        ensureInAttributeMetadata(TagFromName.BodyPartThickness, "BodyPartThickness");
        ensureInAttributeMetadata(TagFromName.BolusDescription, "BolusDescription");
        ensureInAttributeMetadata(TagFromName.BolusID, "BolusID");
        ensureInAttributeMetadata(TagFromName.BoneThermalIndex, "BoneThermalIndex");
        ensureInAttributeMetadata(TagFromName.BorderDensity, "BorderDensity");
        ensureInAttributeMetadata(TagFromName.Borders, "Borders");
        ensureInAttributeMetadata(TagFromName.BoundingBoxAnnotationUnits, "BoundingBoxAnnotationUnits");
        ensureInAttributeMetadata(TagFromName.BoundingBoxBottomRightHandCorner, "BoundingBoxBottomRightHandCorner");
        ensureInAttributeMetadata(TagFromName.BoundingBoxTextHorizontalJustification,
                "BoundingBoxTextHorizontalJustification");
        ensureInAttributeMetadata(TagFromName.BoundingBoxTopLeftHandCorner, "BoundingBoxTopLeftHandCorner");
        ensureInAttributeMetadata(TagFromName.BrachyAccessoryDeviceID, "BrachyAccessoryDeviceID");
        ensureInAttributeMetadata(TagFromName.BrachyAccessoryDeviceName, "BrachyAccessoryDeviceName");
        ensureInAttributeMetadata(TagFromName.BrachyAccessoryDeviceNominalThickness,
                "BrachyAccessoryDeviceNominalThickness");
        ensureInAttributeMetadata(TagFromName.BrachyAccessoryDeviceNominalTransmission,
                "BrachyAccessoryDeviceNominalTransmission");
        ensureInAttributeMetadata(TagFromName.BrachyAccessoryDeviceNumber, "BrachyAccessoryDeviceNumber");
        ensureInAttributeMetadata(TagFromName.BrachyAccessoryDeviceSequence, "BrachyAccessoryDeviceSequence");
        ensureInAttributeMetadata(TagFromName.BrachyAccessoryDeviceType, "BrachyAccessoryDeviceType");
        ensureInAttributeMetadata(TagFromName.BrachyApplicationSetupDose, "BrachyApplicationSetupDose");
        ensureInAttributeMetadata(TagFromName.BrachyApplicationSetupDoseSpecificationPoint,
                "BrachyApplicationSetupDoseSpecificationPoint");
        ensureInAttributeMetadata(TagFromName.BrachyControlPointDeliveredSequence,
                "BrachyControlPointDeliveredSequence");
        ensureInAttributeMetadata(TagFromName.BrachyControlPointSequence, "BrachyControlPointSequence");
        ensureInAttributeMetadata(TagFromName.BrachyReferencedDoseReferenceSequence,
                "BrachyReferencedDoseReferenceSequence");
        ensureInAttributeMetadata(TagFromName.BrachyTreatmentTechnique, "BrachyTreatmentTechnique");
        ensureInAttributeMetadata(TagFromName.BrachyTreatmentType, "BrachyTreatmentType");
        ensureInAttributeMetadata(TagFromName.BranchOfService, "BranchOfService");
        ensureInAttributeMetadata(TagFromName.BreedRegistrationNumber, "BreedRegistrationNumber");
        ensureInAttributeMetadata(TagFromName.BreedRegistrationSequence, "BreedRegistrationSequence");
        ensureInAttributeMetadata(TagFromName.BreedRegistryCodeSequence, "BreedRegistryCodeSequence");
        ensureInAttributeMetadata(TagFromName.BulkMotionCompensationTechnique, "BulkMotionCompensationTechnique");
        ensureInAttributeMetadata(TagFromName.BulkMotionSignalSource, "BulkMotionSignalSource");
        ensureInAttributeMetadata(TagFromName.BulkMotionStatus, "BulkMotionStatus");
        ensureInAttributeMetadata(TagFromName.BurnedInAnnotation, "BurnedInAnnotation");
        ensureInAttributeMetadata(TagFromName.CalciumScoringMassFactorDevice, "CalciumScoringMassFactorDevice");
        ensureInAttributeMetadata(TagFromName.CalciumScoringMassFactorPatient, "CalciumScoringMassFactorPatient");
        ensureInAttributeMetadata(TagFromName.CalculatedAnatomyThickness, "CalculatedAnatomyThickness");
        ensureInAttributeMetadata(TagFromName.CalculatedDoseReferenceDescription, "CalculatedDoseReferenceDescription");
        ensureInAttributeMetadata(TagFromName.CalculatedDoseReferenceDoseValue, "CalculatedDoseReferenceDoseValue");
        ensureInAttributeMetadata(TagFromName.CalculatedDoseReferenceNumber, "CalculatedDoseReferenceNumber");
        ensureInAttributeMetadata(TagFromName.CalculatedDoseReferenceSequence, "CalculatedDoseReferenceSequence");
        ensureInAttributeMetadata(TagFromName.CalibrationDataSequence, "CalibrationDataSequence");
        ensureInAttributeMetadata(TagFromName.CalibrationImage, "CalibrationImage");
        ensureInAttributeMetadata(TagFromName.CalibrationSequence, "CalibrationSequence");
        ensureInAttributeMetadata(TagFromName.CardiacBeatRejectionTechnique, "CardiacBeatRejectionTechnique");
        ensureInAttributeMetadata(TagFromName.CardiacCyclePosition, "CardiacCyclePosition");
        ensureInAttributeMetadata(TagFromName.CardiacFramingType, "CardiacFramingType");
        ensureInAttributeMetadata(TagFromName.CardiacNumberOfImages, "CardiacNumberOfImages");
        ensureInAttributeMetadata(TagFromName.CardiacRRIntervalSpecified, "CardiacRRIntervalSpecified");
        ensureInAttributeMetadata(TagFromName.CardiacSignalSource, "CardiacSignalSource");
        ensureInAttributeMetadata(TagFromName.CardiacSynchronizationSequence, "CardiacSynchronizationSequence");
        ensureInAttributeMetadata(TagFromName.CardiacSynchronizationTechnique, "CardiacSynchronizationTechnique");
        ensureInAttributeMetadata(TagFromName.CArmPositionerTabletopRelationship, "CArmPositionerTabletopRelationship");
        ensureInAttributeMetadata(TagFromName.CassetteID, "CassetteID");
        ensureInAttributeMetadata(TagFromName.CassetteOrientation, "CassetteOrientation");
        ensureInAttributeMetadata(TagFromName.CassetteSize, "CassetteSize");
        ensureInAttributeMetadata(TagFromName.CenterOfCircularCollimator, "CenterOfCircularCollimator");
        ensureInAttributeMetadata(TagFromName.CenterOfCircularExposureControlSensingRegion,
                "CenterOfCircularExposureControlSensingRegion");
        ensureInAttributeMetadata(TagFromName.CenterOfCircularShutter, "CenterOfCircularShutter");
        ensureInAttributeMetadata(TagFromName.CenterOfRotationOffset, "CenterOfRotationOffset");
        ensureInAttributeMetadata(TagFromName.CertificateOfSigner, "CertificateOfSigner");
        ensureInAttributeMetadata(TagFromName.CertificateType, "CertificateType");
        ensureInAttributeMetadata(TagFromName.CertifiedTimestamp, "CertifiedTimestamp");
        ensureInAttributeMetadata(TagFromName.CertifiedTimestampType, "CertifiedTimestampType");
        ensureInAttributeMetadata(TagFromName.ChannelBaseline, "ChannelBaseline");
        ensureInAttributeMetadata(TagFromName.ChannelDefinitionSequence, "ChannelDefinitionSequence");
        ensureInAttributeMetadata(TagFromName.ChannelDerivationDescription, "ChannelDerivationDescription");
        ensureInAttributeMetadata(TagFromName.ChannelDescriptionCodeSequence, "ChannelDescriptionCodeSequence");
        ensureInAttributeMetadata(TagFromName.ChannelDisplaySequence, "ChannelDisplaySequence");
        ensureInAttributeMetadata(TagFromName.ChannelIdentificationCode, "ChannelIdentificationCode");
        ensureInAttributeMetadata(TagFromName.ChannelLabel, "ChannelLabel");
        ensureInAttributeMetadata(TagFromName.ChannelLength, "ChannelLength");
        ensureInAttributeMetadata(TagFromName.ChannelMaximumValue, "ChannelMaximumValue");
        ensureInAttributeMetadata(TagFromName.ChannelMinimumValue, "ChannelMinimumValue");
        ensureInAttributeMetadata(TagFromName.ChannelMode, "ChannelMode");
        ensureInAttributeMetadata(TagFromName.ChannelNumber, "ChannelNumber");
        ensureInAttributeMetadata(TagFromName.ChannelOffset, "ChannelOffset");
        ensureInAttributeMetadata(TagFromName.ChannelPosition, "ChannelPosition");
        ensureInAttributeMetadata(TagFromName.ChannelRecommendedDisplayCIELabValue,
                "ChannelRecommendedDisplayCIELabValue");
        ensureInAttributeMetadata(TagFromName.ChannelSampleSkew, "ChannelSampleSkew");
        ensureInAttributeMetadata(TagFromName.ChannelSensitivity, "ChannelSensitivity");
        ensureInAttributeMetadata(TagFromName.ChannelSensitivityCorrectionFactor, "ChannelSensitivityCorrectionFactor");
        ensureInAttributeMetadata(TagFromName.ChannelSensitivityUnitsSequence, "ChannelSensitivityUnitsSequence");
        ensureInAttributeMetadata(TagFromName.ChannelSequence, "ChannelSequence");
        ensureInAttributeMetadata(TagFromName.ChannelShieldID, "ChannelShieldID");
        ensureInAttributeMetadata(TagFromName.ChannelShieldName, "ChannelShieldName");
        ensureInAttributeMetadata(TagFromName.ChannelShieldNominalThickness, "ChannelShieldNominalThickness");
        ensureInAttributeMetadata(TagFromName.ChannelShieldNominalTransmission, "ChannelShieldNominalTransmission");
        ensureInAttributeMetadata(TagFromName.ChannelShieldNumber, "ChannelShieldNumber");
        ensureInAttributeMetadata(TagFromName.ChannelShieldSequence, "ChannelShieldSequence");
        ensureInAttributeMetadata(TagFromName.ChannelSourceModifiersSequence, "ChannelSourceModifiersSequence");
        ensureInAttributeMetadata(TagFromName.ChannelSourceSequence, "ChannelSourceSequence");
        ensureInAttributeMetadata(TagFromName.ChannelStatus, "ChannelStatus");
        ensureInAttributeMetadata(TagFromName.ChannelTimeSkew, "ChannelTimeSkew");
        ensureInAttributeMetadata(TagFromName.ChannelTotalTime, "ChannelTotalTime");
        ensureInAttributeMetadata(TagFromName.ChemicalShiftMaximumIntegrationLimitInHz,
                "ChemicalShiftMaximumIntegrationLimitInHz");
        ensureInAttributeMetadata(TagFromName.ChemicalShiftMaximumIntegrationLimitInPPM,
                "ChemicalShiftMaximumIntegrationLimitInPPM");
        ensureInAttributeMetadata(TagFromName.ChemicalShiftMinimumIntegrationLimitInHz,
                "ChemicalShiftMinimumIntegrationLimitInHz");
        ensureInAttributeMetadata(TagFromName.ChemicalShiftMinimumIntegrationLimitInPPM,
                "ChemicalShiftMinimumIntegrationLimitInPPM");
        ensureInAttributeMetadata(TagFromName.ChemicalShiftReference, "ChemicalShiftReference");
        ensureInAttributeMetadata(TagFromName.ChemicalShiftSequence, "ChemicalShiftSequence");
        ensureInAttributeMetadata(TagFromName.CineRate, "CineRate");
        ensureInAttributeMetadata(TagFromName.CineRelativeToRealTime, "CineRelativeToRealTime");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialCoordinatingCenterName,
                "ClinicalTrialCoordinatingCenterName");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialProtocolID, "ClinicalTrialProtocolID");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialProtocolName, "ClinicalTrialProtocolName");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialSeriesDescription, "ClinicalTrialSeriesDescription");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialSeriesID, "ClinicalTrialSeriesID");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialSiteID, "ClinicalTrialSiteID");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialSiteName, "ClinicalTrialSiteName");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialSponsorName, "ClinicalTrialSponsorName");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialSubjectID, "ClinicalTrialSubjectID");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialSubjectReadingID, "ClinicalTrialSubjectReadingID");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialTimePointDescription, "ClinicalTrialTimePointDescription");
        ensureInAttributeMetadata(TagFromName.ClinicalTrialTimePointID, "ClinicalTrialTimePointID");
        ensureInAttributeMetadata(TagFromName.CodeLabel, "CodeLabel");
        ensureInAttributeMetadata(TagFromName.CodeMeaning, "CodeMeaning");
        ensureInAttributeMetadata(TagFromName.CodeNumberFormat, "CodeNumberFormat");
        ensureInAttributeMetadata(TagFromName.CodeTableLocation, "CodeTableLocation");
        ensureInAttributeMetadata(TagFromName.CodeValue, "CodeValue");
        ensureInAttributeMetadata(TagFromName.CodingSchemeDesignator, "CodingSchemeDesignator");
        ensureInAttributeMetadata(TagFromName.CodingSchemeExternalID, "CodingSchemeExternalID");
        ensureInAttributeMetadata(TagFromName.CodingSchemeIdentificationSequence, "CodingSchemeIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.CodingSchemeName, "CodingSchemeName");
        ensureInAttributeMetadata(TagFromName.CodingSchemeRegistry, "CodingSchemeRegistry");
        ensureInAttributeMetadata(TagFromName.CodingSchemeResponsibleOrganization,
                "CodingSchemeResponsibleOrganization");
        ensureInAttributeMetadata(TagFromName.CodingSchemeUID, "CodingSchemeUID");
        ensureInAttributeMetadata(TagFromName.CodingSchemeVersion, "CodingSchemeVersion");
        ensureInAttributeMetadata(TagFromName.CoefficientCoding, "CoefficientCoding");
        ensureInAttributeMetadata(TagFromName.CoefficientCodingPointers, "CoefficientCodingPointers");
        ensureInAttributeMetadata(TagFromName.CoefficientsSDDN, "CoefficientsSDDN");
        ensureInAttributeMetadata(TagFromName.CoefficientsSDHN, "CoefficientsSDHN");
        ensureInAttributeMetadata(TagFromName.CoefficientsSDVN, "CoefficientsSDVN");
        ensureInAttributeMetadata(TagFromName.CoincidenceWindowWidth, "CoincidenceWindowWidth");
        ensureInAttributeMetadata(TagFromName.CollationFlag, "CollationFlag");
        ensureInAttributeMetadata(TagFromName.CollimatorGridName, "CollimatorGridName");
        ensureInAttributeMetadata(TagFromName.CollimatorLeftVerticalEdge, "CollimatorLeftVerticalEdge");
        ensureInAttributeMetadata(TagFromName.CollimatorLowerHorizontalEdge, "CollimatorLowerHorizontalEdge");
        ensureInAttributeMetadata(TagFromName.CollimatorRightVerticalEdge, "CollimatorRightVerticalEdge");
        ensureInAttributeMetadata(TagFromName.CollimatorShape, "CollimatorShape");
        ensureInAttributeMetadata(TagFromName.CollimatorShapeSequence, "CollimatorShapeSequence");
        ensureInAttributeMetadata(TagFromName.CollimatorType, "CollimatorType");
        ensureInAttributeMetadata(TagFromName.CollimatorUpperHorizontalEdge, "CollimatorUpperHorizontalEdge");
        ensureInAttributeMetadata(TagFromName.ColorImagePrintingFlag, "ColorImagePrintingFlag");
        ensureInAttributeMetadata(TagFromName.ColumnAngulation, "ColumnAngulation");
        ensureInAttributeMetadata(TagFromName.ColumnAngulationPatient, "ColumnAngulationPatient");
        ensureInAttributeMetadata(TagFromName.ColumnOverlap, "ColumnOverlap");
        ensureInAttributeMetadata(TagFromName.Columns, "Columns");
        ensureInAttributeMetadata(TagFromName.ColumnsForNthOrderCoefficients, "ColumnsForNthOrderCoefficients");
        ensureInAttributeMetadata(TagFromName.CommandField, "CommandField");
        ensureInAttributeMetadata(TagFromName.CommandGroupLength, "CommandGroupLength");
        ensureInAttributeMetadata(TagFromName.CommandLengthToEnd, "CommandLengthToEnd");
        ensureInAttributeMetadata(TagFromName.CommandRecognitionCode, "CommandRecognitionCode");
        ensureInAttributeMetadata(TagFromName.CommentsOnPerformedProcedureStep, "CommentsOnPerformedProcedureStep");
        ensureInAttributeMetadata(TagFromName.CommentsOnRadiationDose, "CommentsOnRadiationDose");
        ensureInAttributeMetadata(TagFromName.CommentsOnScheduledProcedureStep, "CommentsOnScheduledProcedureStep");
        ensureInAttributeMetadata(TagFromName.CompensatorColumnOffset, "CompensatorColumnOffset");
        ensureInAttributeMetadata(TagFromName.CompensatorColumns, "CompensatorColumns");
        ensureInAttributeMetadata(TagFromName.CompensatorDescription, "CompensatorDescription");
        ensureInAttributeMetadata(TagFromName.CompensatorDivergence, "CompensatorDivergence");
        ensureInAttributeMetadata(TagFromName.CompensatorID, "CompensatorID");
        ensureInAttributeMetadata(TagFromName.CompensatorMillingToolDiameter, "CompensatorMillingToolDiameter");
        ensureInAttributeMetadata(TagFromName.CompensatorMountingPosition, "CompensatorMountingPosition");
        ensureInAttributeMetadata(TagFromName.CompensatorNumber, "CompensatorNumber");
        ensureInAttributeMetadata(TagFromName.CompensatorPixelSpacing, "CompensatorPixelSpacing");
        ensureInAttributeMetadata(TagFromName.CompensatorPosition, "CompensatorPosition");
        ensureInAttributeMetadata(TagFromName.CompensatorRelativeStoppingPowerRatio,
                "CompensatorRelativeStoppingPowerRatio");
        ensureInAttributeMetadata(TagFromName.CompensatorRows, "CompensatorRows");
        ensureInAttributeMetadata(TagFromName.CompensatorSequence, "CompensatorSequence");
        ensureInAttributeMetadata(TagFromName.CompensatorThicknessData, "CompensatorThicknessData");
        ensureInAttributeMetadata(TagFromName.CompensatorTransmissionData, "CompensatorTransmissionData");
        ensureInAttributeMetadata(TagFromName.CompensatorType, "CompensatorType");
        ensureInAttributeMetadata(TagFromName.CompletionFlag, "CompletionFlag");
        ensureInAttributeMetadata(TagFromName.CompletionFlagDescription, "CompletionFlagDescription");
        ensureInAttributeMetadata(TagFromName.ComplexImageComponent, "ComplexImageComponent");
        ensureInAttributeMetadata(TagFromName.CompressionCode, "CompressionCode");
        ensureInAttributeMetadata(TagFromName.CompressionDescription, "CompressionDescription");
        ensureInAttributeMetadata(TagFromName.CompressionForce, "CompressionForce");
        ensureInAttributeMetadata(TagFromName.CompressionLabel, "CompressionLabel");
        ensureInAttributeMetadata(TagFromName.CompressionOriginator, "CompressionOriginator");
        ensureInAttributeMetadata(TagFromName.CompressionRecognitionCode, "CompressionRecognitionCode");
        ensureInAttributeMetadata(TagFromName.CompressionSequence, "CompressionSequence");
        ensureInAttributeMetadata(TagFromName.CompressionStepPointers, "CompressionStepPointers");
        ensureInAttributeMetadata(TagFromName.ConcatenationFrameOffsetNumber, "ConcatenationFrameOffsetNumber");
        ensureInAttributeMetadata(TagFromName.ConcatenationUID, "ConcatenationUID");
        ensureInAttributeMetadata(TagFromName.ConceptCodeSequence, "ConceptCodeSequence");
        ensureInAttributeMetadata(TagFromName.ConceptNameCodeSequence, "ConceptNameCodeSequence");
        ensureInAttributeMetadata(TagFromName.ConfidentialityCode, "ConfidentialityCode");
        ensureInAttributeMetadata(TagFromName.ConfidentialityConstraintOnPatientDataDescription,
                "ConfidentialityConstraintOnPatientDataDescription");
        ensureInAttributeMetadata(TagFromName.ConfigurationInformation, "ConfigurationInformation");
        ensureInAttributeMetadata(TagFromName.ConfigurationInformationDescription,
                "ConfigurationInformationDescription");
        ensureInAttributeMetadata(TagFromName.ConstantVolumeFlag, "ConstantVolumeFlag");
        ensureInAttributeMetadata(TagFromName.ConstraintWeight, "ConstraintWeight");
        ensureInAttributeMetadata(TagFromName.ContentCreatorsIdentificationCodeSequence,
                "ContentCreatorsIdentificationCodeSequence");
        ensureInAttributeMetadata(TagFromName.ContentCreatorsName, "ContentCreatorsName");
        ensureInAttributeMetadata(TagFromName.ContentDate, "ContentDate");
        ensureInAttributeMetadata(TagFromName.ContentDescription, "ContentDescription");
        ensureInAttributeMetadata(TagFromName.ContentItemModifierSequence, "ContentItemModifierSequence");
        ensureInAttributeMetadata(TagFromName.ContentLabel, "ContentLabel");
        ensureInAttributeMetadata(TagFromName.ContentQualification, "ContentQualification");
        ensureInAttributeMetadata(TagFromName.ContentSequence, "ContentSequence");
        ensureInAttributeMetadata(TagFromName.ContentTemplateSequence, "ContentTemplateSequence");
        ensureInAttributeMetadata(TagFromName.ContentTime, "ContentTime");
        ensureInAttributeMetadata(TagFromName.ContextGroupExtensionCreatorUID, "ContextGroupExtensionCreatorUID");
        ensureInAttributeMetadata(TagFromName.ContextGroupExtensionFlag, "ContextGroupExtensionFlag");
        ensureInAttributeMetadata(TagFromName.ContextGroupLocalVersion, "ContextGroupLocalVersion");
        ensureInAttributeMetadata(TagFromName.ContextGroupVersion, "ContextGroupVersion");
        ensureInAttributeMetadata(TagFromName.ContextIdentifier, "ContextIdentifier");
        // ensureInAttributeMetadata(TagFromName.ContextUID, "ContextUID");
        ensureInAttributeMetadata(TagFromName.ContinuityOfContent, "ContinuityOfContent");
        ensureInAttributeMetadata(TagFromName.ContourData, "ContourData");
        ensureInAttributeMetadata(TagFromName.ContourGeometricType, "ContourGeometricType");
        ensureInAttributeMetadata(TagFromName.ContourImageSequence, "ContourImageSequence");
        ensureInAttributeMetadata(TagFromName.ContourNumber, "ContourNumber");
        ensureInAttributeMetadata(TagFromName.ContourOffsetVector, "ContourOffsetVector");
        ensureInAttributeMetadata(TagFromName.ContourSequence, "ContourSequence");
        ensureInAttributeMetadata(TagFromName.ContourSlabThickness, "ContourSlabThickness");
        ensureInAttributeMetadata(TagFromName.ContourUncertaintyRadius, "ContourUncertaintyRadius");
        ensureInAttributeMetadata(TagFromName.ContrastAdministrationProfileSequence,
                "ContrastAdministrationProfileSequence");
        ensureInAttributeMetadata(TagFromName.ContrastBolusAdministrationRouteSequence,
                "ContrastBolusAdministrationRouteSequence");
        ensureInAttributeMetadata(TagFromName.ContrastBolusAgent, "ContrastBolusAgent");
        ensureInAttributeMetadata(TagFromName.ContrastBolusAgentAdministered, "ContrastBolusAgentAdministered");
        ensureInAttributeMetadata(TagFromName.ContrastBolusAgentDetected, "ContrastBolusAgentDetected");
        ensureInAttributeMetadata(TagFromName.ContrastBolusAgentNumber, "ContrastBolusAgentNumber");
        ensureInAttributeMetadata(TagFromName.ContrastBolusAgentPhase, "ContrastBolusAgentPhase");
        ensureInAttributeMetadata(TagFromName.ContrastBolusAgentSequence, "ContrastBolusAgentSequence");
        ensureInAttributeMetadata(TagFromName.ContrastBolusIngredient, "ContrastBolusIngredient");
        ensureInAttributeMetadata(TagFromName.ContrastBolusIngredientCodeSequence,
                "ContrastBolusIngredientCodeSequence");
        ensureInAttributeMetadata(TagFromName.ContrastBolusIngredientConcentration,
                "ContrastBolusIngredientConcentration");
        ensureInAttributeMetadata(TagFromName.ContrastBolusIngredientOpaque, "ContrastBolusIngredientOpaque");
        ensureInAttributeMetadata(TagFromName.ContrastBolusRoute, "ContrastBolusRoute");
        ensureInAttributeMetadata(TagFromName.ContrastBolusStartTime, "ContrastBolusStartTime");
        ensureInAttributeMetadata(TagFromName.ContrastBolusStopTime, "ContrastBolusStopTime");
        ensureInAttributeMetadata(TagFromName.ContrastBolusTotalDose, "ContrastBolusTotalDose");
        ensureInAttributeMetadata(TagFromName.ContrastBolusUsageSequence, "ContrastBolusUsageSequence");
        ensureInAttributeMetadata(TagFromName.ContrastBolusVolume, "ContrastBolusVolume");
        ensureInAttributeMetadata(TagFromName.ContrastFlowDuration, "ContrastFlowDuration");
        ensureInAttributeMetadata(TagFromName.ContrastFlowRate, "ContrastFlowRate");
        ensureInAttributeMetadata(TagFromName.ContrastFrameAveraging, "ContrastFrameAveraging");
        ensureInAttributeMetadata(TagFromName.ContributingEquipmentSequence, "ContributingEquipmentSequence");
        ensureInAttributeMetadata(TagFromName.ContributingSOPInstancesReferenceSequence,
                "ContributingSOPInstancesReferenceSequence");
        ensureInAttributeMetadata(TagFromName.ContributingSourcesSequence, "ContributingSourcesSequence");
        ensureInAttributeMetadata(TagFromName.ContributionDateTime, "ContributionDateTime");
        ensureInAttributeMetadata(TagFromName.ContributionDescription, "ContributionDescription");
        ensureInAttributeMetadata(TagFromName.ControlPoint3DPosition, "ControlPoint3DPosition");
        ensureInAttributeMetadata(TagFromName.ControlPointDeliverySequence, "ControlPointDeliverySequence");
        ensureInAttributeMetadata(TagFromName.ControlPointIndex, "ControlPointIndex");
        ensureInAttributeMetadata(TagFromName.ControlPointOrientation, "ControlPointOrientation");
        ensureInAttributeMetadata(TagFromName.ControlPointRelativePosition, "ControlPointRelativePosition");
        ensureInAttributeMetadata(TagFromName.ControlPointSequence, "ControlPointSequence");
        ensureInAttributeMetadata(TagFromName.ConversionType, "ConversionType");
        ensureInAttributeMetadata(TagFromName.ConvolutionKernel, "ConvolutionKernel");
        ensureInAttributeMetadata(TagFromName.ConvolutionKernelGroup, "ConvolutionKernelGroup");
        // ensureInAttributeMetadata(TagFromName.CoordinatesSetGeometricTypeTrial,
        // "CoordinatesSetGeometricTypeTrial");
        ensureInAttributeMetadata(TagFromName.CoordinateStartValue, "CoordinateStartValue");
        ensureInAttributeMetadata(TagFromName.CoordinateStepValue, "CoordinateStepValue");
        ensureInAttributeMetadata(TagFromName.CoordinateSystemAxisCodeSequence, "CoordinateSystemAxisCodeSequence");
        ensureInAttributeMetadata(TagFromName.Copies, "Copies");
        ensureInAttributeMetadata(TagFromName.CorrectedImage, "CorrectedImage");
        ensureInAttributeMetadata(TagFromName.CorrectedParameterSequence, "CorrectedParameterSequence");
        ensureInAttributeMetadata(TagFromName.CorrectionValue, "CorrectionValue");
        ensureInAttributeMetadata(TagFromName.CountRate, "CountRate");
        ensureInAttributeMetadata(TagFromName.CountryOfResidence, "CountryOfResidence");
        ensureInAttributeMetadata(TagFromName.CountsAccumulated, "CountsAccumulated");
        ensureInAttributeMetadata(TagFromName.CountsIncluded, "CountsIncluded");
        ensureInAttributeMetadata(TagFromName.CountsSource, "CountsSource");
        ensureInAttributeMetadata(TagFromName.CoverageOfKSpace, "CoverageOfKSpace");
        ensureInAttributeMetadata(TagFromName.CranialThermalIndex, "CranialThermalIndex");
        ensureInAttributeMetadata(TagFromName.CreationDate, "CreationDate");
        ensureInAttributeMetadata(TagFromName.CreationTime, "CreationTime");
        ensureInAttributeMetadata(TagFromName.CreatorVersionUID, "CreatorVersionUID");
        ensureInAttributeMetadata(TagFromName.CTAcquisitionDetailsSequence, "CTAcquisitionDetailsSequence");
        ensureInAttributeMetadata(TagFromName.CTAcquisitionTypeSequence, "CTAcquisitionTypeSequence");
        ensureInAttributeMetadata(TagFromName.CTAdditionalXRaySourceSequence, "CTAdditionalXRaySourceSequence");
        ensureInAttributeMetadata(TagFromName.CTDIPhantomTypeCodeSequence, "CTDIPhantomTypeCodeSequence");
        ensureInAttributeMetadata(TagFromName.CTDIvol, "CTDIvol");
        ensureInAttributeMetadata(TagFromName.CTExposureSequence, "CTExposureSequence");
        ensureInAttributeMetadata(TagFromName.CTGeometrySequence, "CTGeometrySequence");
        ensureInAttributeMetadata(TagFromName.CTImageFrameTypeSequence, "CTImageFrameTypeSequence");
        ensureInAttributeMetadata(TagFromName.CTPositionSequence, "CTPositionSequence");
        ensureInAttributeMetadata(TagFromName.CTReconstructionSequence, "CTReconstructionSequence");
        ensureInAttributeMetadata(TagFromName.CTTableDynamicsSequence, "CTTableDynamicsSequence");
        ensureInAttributeMetadata(TagFromName.CTXRayDetailsSequence, "CTXRayDetailsSequence");
        ensureInAttributeMetadata(TagFromName.CumulativeDoseReferenceCoefficient, "CumulativeDoseReferenceCoefficient");
        ensureInAttributeMetadata(TagFromName.CumulativeDoseToDoseReference, "CumulativeDoseToDoseReference");
        ensureInAttributeMetadata(TagFromName.CumulativeMetersetWeight, "CumulativeMetersetWeight");
        ensureInAttributeMetadata(TagFromName.CumulativeTimeWeight, "CumulativeTimeWeight");
        ensureInAttributeMetadata(TagFromName.CurrentFractionNumber, "CurrentFractionNumber");
        // ensureInAttributeMetadata(TagFromName.CurrentObserverTrial,
        // "CurrentObserverTrial");
        ensureInAttributeMetadata(TagFromName.CurrentPatientLocation, "CurrentPatientLocation");
        ensureInAttributeMetadata(TagFromName.CurrentRequestedProcedureEvidenceSequence,
                "CurrentRequestedProcedureEvidenceSequence");
        ensureInAttributeMetadata(TagFromName.CurrentTreatmentStatus, "CurrentTreatmentStatus");
        ensureInAttributeMetadata(TagFromName.CurveActivationLayer, "CurveActivationLayer");
        ensureInAttributeMetadata(TagFromName.CurveData, "CurveData");
        ensureInAttributeMetadata(TagFromName.CurveDataDescriptor, "CurveDataDescriptor");
        ensureInAttributeMetadata(TagFromName.CurveDate, "CurveDate");
        ensureInAttributeMetadata(TagFromName.CurveDescription, "CurveDescription");
        ensureInAttributeMetadata(TagFromName.CurveDimensions, "CurveDimensions");
        ensureInAttributeMetadata(TagFromName.CurveLabel, "CurveLabel");
        ensureInAttributeMetadata(TagFromName.CurveNumber, "CurveNumber");
        ensureInAttributeMetadata(TagFromName.CurveRange, "CurveRange");
        ensureInAttributeMetadata(TagFromName.CurveReferencedOverlayGroup, "CurveReferencedOverlayGroup");
        ensureInAttributeMetadata(TagFromName.CurveReferencedOverlaySequence, "CurveReferencedOverlaySequence");
        ensureInAttributeMetadata(TagFromName.CurveTime, "CurveTime");
        ensureInAttributeMetadata(TagFromName.CustodialOrganizationSequence, "CustodialOrganizationSequence");
        ensureInAttributeMetadata(TagFromName.CylinderAxis, "CylinderAxis");
        ensureInAttributeMetadata(TagFromName.CylinderLensPower, "CylinderLensPower");
        ensureInAttributeMetadata(TagFromName.DataBlock, "DataBlock");
        ensureInAttributeMetadata(TagFromName.DataBlockDescription, "DataBlockDescription");
        ensureInAttributeMetadata(TagFromName.DataCollectionCenterPatient, "DataCollectionCenterPatient");
        ensureInAttributeMetadata(TagFromName.DataCollectionDiameter, "DataCollectionDiameter");
        ensureInAttributeMetadata(TagFromName.DataElementsSigned, "DataElementsSigned");
        ensureInAttributeMetadata(TagFromName.DataInformationSequence, "DataInformationSequence");
        ensureInAttributeMetadata(TagFromName.DataPointColumns, "DataPointColumns");
        ensureInAttributeMetadata(TagFromName.DataPointRows, "DataPointRows");
        ensureInAttributeMetadata(TagFromName.DataRepresentation, "DataRepresentation");
        ensureInAttributeMetadata(TagFromName.DataSetTrailingPadding, "DataSetTrailingPadding");
        ensureInAttributeMetadata(TagFromName.DataSetType, "DataSetType");
        ensureInAttributeMetadata(TagFromName.DataValueRepresentation, "DataValueRepresentation");
        ensureInAttributeMetadata(TagFromName.Date, "Date");
        // ensureInAttributeMetadata(TagFromName.DateOfDocumentOrVerbalTransactionTrial,
        // "DateOfDocumentOrVerbalTransactionTrial");
        ensureInAttributeMetadata(TagFromName.DateOfLastCalibration, "DateOfLastCalibration");
        ensureInAttributeMetadata(TagFromName.DateOfLastDetectorCalibration, "DateOfLastDetectorCalibration");
        ensureInAttributeMetadata(TagFromName.DateOfSecondaryCapture, "DateOfSecondaryCapture");
        ensureInAttributeMetadata(TagFromName.DateTime, "DateTime");
        ensureInAttributeMetadata(TagFromName.dBdt, "dBdt");
        ensureInAttributeMetadata(TagFromName.DCTLabel, "DCTLabel");
        ensureInAttributeMetadata(TagFromName.DeadTimeCorrectionFlag, "DeadTimeCorrectionFlag");
        ensureInAttributeMetadata(TagFromName.DeadTimeFactor, "DeadTimeFactor");
        ensureInAttributeMetadata(TagFromName.DecayCorrection, "DecayCorrection");
        ensureInAttributeMetadata(TagFromName.DecayFactor, "DecayFactor");
        ensureInAttributeMetadata(TagFromName.DecimateCropResult, "DecimateCropResult");
        ensureInAttributeMetadata(TagFromName.DecoupledNucleus, "DecoupledNucleus");
        ensureInAttributeMetadata(TagFromName.Decoupling, "Decoupling");
        ensureInAttributeMetadata(TagFromName.DecouplingChemicalShiftReference, "DecouplingChemicalShiftReference");
        ensureInAttributeMetadata(TagFromName.DecouplingFrequency, "DecouplingFrequency");
        ensureInAttributeMetadata(TagFromName.DecouplingMethod, "DecouplingMethod");
        ensureInAttributeMetadata(TagFromName.DefaultMagnificationType, "DefaultMagnificationType");
        ensureInAttributeMetadata(TagFromName.DefaultPrinterResolutionID, "DefaultPrinterResolutionID");
        ensureInAttributeMetadata(TagFromName.DefaultSmoothingType, "DefaultSmoothingType");
        ensureInAttributeMetadata(TagFromName.DeformableRegistrationGridSequence, "DeformableRegistrationGridSequence");
        ensureInAttributeMetadata(TagFromName.DeformableRegistrationSequence, "DeformableRegistrationSequence");
        ensureInAttributeMetadata(TagFromName.DegreeOfDilation, "DegreeOfDilation");
        ensureInAttributeMetadata(TagFromName.DeidentificationMethod, "DeidentificationMethod");
        ensureInAttributeMetadata(TagFromName.DeidentificationMethodCodeSequence, "DeidentificationMethodCodeSequence");
        ensureInAttributeMetadata(TagFromName.DeliveredChannelTotalTime, "DeliveredChannelTotalTime");
        ensureInAttributeMetadata(TagFromName.DeliveredMeterset, "DeliveredMeterset");
        ensureInAttributeMetadata(TagFromName.DeliveredNumberOfPulses, "DeliveredNumberOfPulses");
        ensureInAttributeMetadata(TagFromName.DeliveredPrimaryMeterset, "DeliveredPrimaryMeterset");
        ensureInAttributeMetadata(TagFromName.DeliveredPulseRepetitionInterval, "DeliveredPulseRepetitionInterval");
        ensureInAttributeMetadata(TagFromName.DeliveredSecondaryMeterset, "DeliveredSecondaryMeterset");
        ensureInAttributeMetadata(TagFromName.DeliveredTreatmentTime, "DeliveredTreatmentTime");
        ensureInAttributeMetadata(TagFromName.DeliveryMaximumDose, "DeliveryMaximumDose");
        ensureInAttributeMetadata(TagFromName.DeliveryWarningDose, "DeliveryWarningDose");
        ensureInAttributeMetadata(TagFromName.DepthOfScanField, "DepthOfScanField");
        ensureInAttributeMetadata(TagFromName.DepthOfTransverseImage, "DepthOfTransverseImage");
        ensureInAttributeMetadata(TagFromName.DepthSpatialResolution, "DepthSpatialResolution");
        ensureInAttributeMetadata(TagFromName.DerivationCodeSequence, "DerivationCodeSequence");
        ensureInAttributeMetadata(TagFromName.DerivationDescription, "DerivationDescription");
        ensureInAttributeMetadata(TagFromName.DerivationImageSequence, "DerivationImageSequence");
        ensureInAttributeMetadata(TagFromName.DestinationAE, "DestinationAE");
        ensureInAttributeMetadata(TagFromName.DetectorActivationOffsetFromExposure,
                "DetectorActivationOffsetFromExposure");
        ensureInAttributeMetadata(TagFromName.DetectorActiveDimensions, "DetectorActiveDimensions");
        ensureInAttributeMetadata(TagFromName.DetectorActiveOrigin, "DetectorActiveOrigin");
        ensureInAttributeMetadata(TagFromName.DetectorActiveShape, "DetectorActiveShape");
        ensureInAttributeMetadata(TagFromName.DetectorActiveTime, "DetectorActiveTime");
        ensureInAttributeMetadata(TagFromName.DetectorBinning, "DetectorBinning");
        ensureInAttributeMetadata(TagFromName.DetectorConditionsNominalFlag, "DetectorConditionsNominalFlag");
        ensureInAttributeMetadata(TagFromName.DetectorConfiguration, "DetectorConfiguration");
        ensureInAttributeMetadata(TagFromName.DetectorDescription, "DetectorDescription");
        ensureInAttributeMetadata(TagFromName.DetectorElementPhysicalSize, "DetectorElementPhysicalSize");
        ensureInAttributeMetadata(TagFromName.DetectorElementSize, "DetectorElementSize");
        ensureInAttributeMetadata(TagFromName.DetectorElementSpacing, "DetectorElementSpacing");
        ensureInAttributeMetadata(TagFromName.DetectorID, "DetectorID");
        ensureInAttributeMetadata(TagFromName.DetectorInformationSequence, "DetectorInformationSequence");
        ensureInAttributeMetadata(TagFromName.DetectorLinesOfResponseUsed, "DetectorLinesOfResponseUsed");
        ensureInAttributeMetadata(TagFromName.DetectorManufacturerModelName, "DetectorManufacturerModelName");
        ensureInAttributeMetadata(TagFromName.DetectorManufacturerName, "DetectorManufacturerName");
        ensureInAttributeMetadata(TagFromName.DetectorMode, "DetectorMode");
        ensureInAttributeMetadata(TagFromName.DetectorPrimaryAngle, "DetectorPrimaryAngle");
        ensureInAttributeMetadata(TagFromName.DetectorSecondaryAngle, "DetectorSecondaryAngle");
        ensureInAttributeMetadata(TagFromName.DetectorTemperature, "DetectorTemperature");
        ensureInAttributeMetadata(TagFromName.DetectorTimeSinceLastExposure, "DetectorTimeSinceLastExposure");
        ensureInAttributeMetadata(TagFromName.DetectorType, "DetectorType");
        ensureInAttributeMetadata(TagFromName.DetectorVector, "DetectorVector");
        ensureInAttributeMetadata(TagFromName.DeviceDescription, "DeviceDescription");
        ensureInAttributeMetadata(TagFromName.DeviceDiameter, "DeviceDiameter");
        ensureInAttributeMetadata(TagFromName.DeviceDiameterUnits, "DeviceDiameterUnits");
        ensureInAttributeMetadata(TagFromName.DeviceLength, "DeviceLength");
        ensureInAttributeMetadata(TagFromName.DeviceSequence, "DeviceSequence");
        ensureInAttributeMetadata(TagFromName.DeviceSerialNumber, "DeviceSerialNumber");
        ensureInAttributeMetadata(TagFromName.DeviceUID, "DeviceUID");
        ensureInAttributeMetadata(TagFromName.DeviceVolume, "DeviceVolume");
        ensureInAttributeMetadata(TagFromName.DialogReceiver, "DialogReceiver");
        ensureInAttributeMetadata(TagFromName.DiaphragmPosition, "DiaphragmPosition");
        ensureInAttributeMetadata(TagFromName.DiffusionAnisotropyType, "DiffusionAnisotropyType");
        ensureInAttributeMetadata(TagFromName.DiffusionBMatrixSequence, "DiffusionBMatrixSequence");
        ensureInAttributeMetadata(TagFromName.DiffusionBMatrixValueXX, "DiffusionBMatrixValueXX");
        ensureInAttributeMetadata(TagFromName.DiffusionBMatrixValueXY, "DiffusionBMatrixValueXY");
        ensureInAttributeMetadata(TagFromName.DiffusionBMatrixValueXZ, "DiffusionBMatrixValueXZ");
        ensureInAttributeMetadata(TagFromName.DiffusionBMatrixValueYY, "DiffusionBMatrixValueYY");
        ensureInAttributeMetadata(TagFromName.DiffusionBMatrixValueYZ, "DiffusionBMatrixValueYZ");
        ensureInAttributeMetadata(TagFromName.DiffusionBMatrixValueZZ, "DiffusionBMatrixValueZZ");
        ensureInAttributeMetadata(TagFromName.DiffusionBValue, "DiffusionBValue");
        ensureInAttributeMetadata(TagFromName.DiffusionDirectionality, "DiffusionDirectionality");
        ensureInAttributeMetadata(TagFromName.DiffusionGradientDirectionSequence, "DiffusionGradientDirectionSequence");
        ensureInAttributeMetadata(TagFromName.DiffusionGradientOrientation, "DiffusionGradientOrientation");
        ensureInAttributeMetadata(TagFromName.DigitalImageFormatAcquired, "DigitalImageFormatAcquired");
        ensureInAttributeMetadata(TagFromName.DigitalSignatureDateTime, "DigitalSignatureDateTime");
        ensureInAttributeMetadata(TagFromName.DigitalSignaturePurposeCodeSequence,
                "DigitalSignaturePurposeCodeSequence");
        ensureInAttributeMetadata(TagFromName.DigitalSignaturesSequence, "DigitalSignaturesSequence");
        ensureInAttributeMetadata(TagFromName.DigitalSignatureUID, "DigitalSignatureUID");
        ensureInAttributeMetadata(TagFromName.DigitizingDeviceTransportDirection, "DigitizingDeviceTransportDirection");
        ensureInAttributeMetadata(TagFromName.DimensionDescriptionLabel, "DimensionDescriptionLabel");
        ensureInAttributeMetadata(TagFromName.DimensionIndexPointer, "DimensionIndexPointer");
        ensureInAttributeMetadata(TagFromName.DimensionIndexPrivateCreator, "DimensionIndexPrivateCreator");
        ensureInAttributeMetadata(TagFromName.DimensionIndexSequence, "DimensionIndexSequence");
        ensureInAttributeMetadata(TagFromName.DimensionIndexValues, "DimensionIndexValues");
        ensureInAttributeMetadata(TagFromName.DimensionOrganizationSequence, "DimensionOrganizationSequence");
        ensureInAttributeMetadata(TagFromName.DimensionOrganizationUID, "DimensionOrganizationUID");
        ensureInAttributeMetadata(TagFromName.DirectoryRecordSequence, "DirectoryRecordSequence");
        ensureInAttributeMetadata(TagFromName.DirectoryRecordType, "DirectoryRecordType");
        ensureInAttributeMetadata(TagFromName.DischargeDate, "DischargeDate");
        ensureInAttributeMetadata(TagFromName.DischargeDiagnosisCodeSequence, "DischargeDiagnosisCodeSequence");
        ensureInAttributeMetadata(TagFromName.DischargeDiagnosisDescription, "DischargeDiagnosisDescription");
        ensureInAttributeMetadata(TagFromName.DischargeTime, "DischargeTime");
        ensureInAttributeMetadata(TagFromName.DisplayedAreaBottomRightHandCorner, "DisplayedAreaBottomRightHandCorner");
        ensureInAttributeMetadata(TagFromName.DisplayedAreaBottomRightHandCornerTrial,
                "DisplayedAreaBottomRightHandCornerTrial");
        ensureInAttributeMetadata(TagFromName.DisplayedAreaSelectionSequence, "DisplayedAreaSelectionSequence");
        ensureInAttributeMetadata(TagFromName.DisplayedAreaTopLeftHandCorner, "DisplayedAreaTopLeftHandCorner");
        ensureInAttributeMetadata(TagFromName.DisplayedAreaTopLeftHandCornerTrial,
                "DisplayedAreaTopLeftHandCornerTrial");
        ensureInAttributeMetadata(TagFromName.DisplayEnvironmentSpatialPosition, "DisplayEnvironmentSpatialPosition");
        ensureInAttributeMetadata(TagFromName.DisplayFilterPercentage, "DisplayFilterPercentage");
        ensureInAttributeMetadata(TagFromName.DisplayFormat, "DisplayFormat");
        ensureInAttributeMetadata(TagFromName.DisplaySetHorizontalJustification, "DisplaySetHorizontalJustification");
        ensureInAttributeMetadata(TagFromName.DisplaySetLabel, "DisplaySetLabel");
        ensureInAttributeMetadata(TagFromName.DisplaySetNumber, "DisplaySetNumber");
        ensureInAttributeMetadata(TagFromName.DisplaySetPatientOrientation, "DisplaySetPatientOrientation");
        ensureInAttributeMetadata(TagFromName.DisplaySetPresentationGroup, "DisplaySetPresentationGroup");
        ensureInAttributeMetadata(TagFromName.DisplaySetPresentationGroupDescription,
                "DisplaySetPresentationGroupDescription");
        ensureInAttributeMetadata(TagFromName.DisplaySetScrollingGroup, "DisplaySetScrollingGroup");
        ensureInAttributeMetadata(TagFromName.DisplaySetsSequence, "DisplaySetsSequence");
        ensureInAttributeMetadata(TagFromName.DisplaySetVerticalJustification, "DisplaySetVerticalJustification");
        ensureInAttributeMetadata(TagFromName.DisplayShadingFlag, "DisplayShadingFlag");
        ensureInAttributeMetadata(TagFromName.DisplayWindowLabelVector, "DisplayWindowLabelVector");
        ensureInAttributeMetadata(TagFromName.DistanceObjectToTableTop, "DistanceObjectToTableTop");
        ensureInAttributeMetadata(TagFromName.DistanceReceptorPlaneToDetectorHousing,
                "DistanceReceptorPlaneToDetectorHousing");
        ensureInAttributeMetadata(TagFromName.DistanceSourceToDataCollectionCenter,
                "DistanceSourceToDataCollectionCenter");
        ensureInAttributeMetadata(TagFromName.DistanceSourceToDetector, "DistanceSourceToDetector");
        ensureInAttributeMetadata(TagFromName.DistanceSourceToEntrance, "DistanceSourceToEntrance");
        ensureInAttributeMetadata(TagFromName.DistanceSourceToIsocenter, "DistanceSourceToIsocenter");
        ensureInAttributeMetadata(TagFromName.DistanceSourceToPatient, "DistanceSourceToPatient");
        ensureInAttributeMetadata(TagFromName.DistanceSourceToSupport, "DistanceSourceToSupport");
        ensureInAttributeMetadata(TagFromName.DistributionAddress, "DistributionAddress");
        ensureInAttributeMetadata(TagFromName.DistributionName, "DistributionName");
        // ensureInAttributeMetadata(TagFromName.DocumentAuthorIdentifierCodeSequenceTrial,
        // "DocumentAuthorIdentifierCodeSequenceTrial");
        // ensureInAttributeMetadata(TagFromName.DocumentAuthorTrial,
        // "DocumentAuthorTrial");
        // ensureInAttributeMetadata(TagFromName.DocumentIdentifierCodeSequenceTrial,
        // "DocumentIdentifierCodeSequenceTrial");
        // ensureInAttributeMetadata(TagFromName.DocumentingObserverIdentifierCodeSequenceTrial,
        // "DocumentingObserverIdentifierCodeSequenceTrial");
        // ensureInAttributeMetadata(TagFromName.DocumentingOrganizationIdentifierCodeSequenceTrial,
        // "DocumentingOrganizationIdentifierCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.DocumentTitle, "DocumentTitle");
        ensureInAttributeMetadata(TagFromName.DopplerCorrectionAngle, "DopplerCorrectionAngle");
        ensureInAttributeMetadata(TagFromName.DopplerSampleVolumeXPosition, "DopplerSampleVolumeXPosition");
        ensureInAttributeMetadata(TagFromName.DopplerSampleVolumeXPositionRetired,
                "DopplerSampleVolumeXPositionRetired");
        ensureInAttributeMetadata(TagFromName.DopplerSampleVolumeYPosition, "DopplerSampleVolumeYPosition");
        ensureInAttributeMetadata(TagFromName.DopplerSampleVolumeYPositionRetired,
                "DopplerSampleVolumeYPositionRetired");
        ensureInAttributeMetadata(TagFromName.DoseCalibrationFactor, "DoseCalibrationFactor");
        ensureInAttributeMetadata(TagFromName.DoseComment, "DoseComment");
        ensureInAttributeMetadata(TagFromName.DoseGridScaling, "DoseGridScaling");
        ensureInAttributeMetadata(TagFromName.DoseRateDelivered, "DoseRateDelivered");
        ensureInAttributeMetadata(TagFromName.DoseRateSet, "DoseRateSet");
        ensureInAttributeMetadata(TagFromName.DoseReferenceDescription, "DoseReferenceDescription");
        ensureInAttributeMetadata(TagFromName.DoseReferenceNumber, "DoseReferenceNumber");
        ensureInAttributeMetadata(TagFromName.DoseReferencePointCoordinates, "DoseReferencePointCoordinates");
        ensureInAttributeMetadata(TagFromName.DoseReferenceSequence, "DoseReferenceSequence");
        ensureInAttributeMetadata(TagFromName.DoseReferenceStructureType, "DoseReferenceStructureType");
        ensureInAttributeMetadata(TagFromName.DoseReferenceType, "DoseReferenceType");
        ensureInAttributeMetadata(TagFromName.DoseReferenceUID, "DoseReferenceUID");
        ensureInAttributeMetadata(TagFromName.DoseSummationType, "DoseSummationType");
        ensureInAttributeMetadata(TagFromName.DoseType, "DoseType");
        ensureInAttributeMetadata(TagFromName.DoseUnits, "DoseUnits");
        ensureInAttributeMetadata(TagFromName.DoseValue, "DoseValue");
        ensureInAttributeMetadata(TagFromName.DVHData, "DVHData");
        ensureInAttributeMetadata(TagFromName.DVHDoseScaling, "DVHDoseScaling");
        ensureInAttributeMetadata(TagFromName.DVHMaximumDose, "DVHMaximumDose");
        ensureInAttributeMetadata(TagFromName.DVHMeanDose, "DVHMeanDose");
        ensureInAttributeMetadata(TagFromName.DVHMinimumDose, "DVHMinimumDose");
        ensureInAttributeMetadata(TagFromName.DVHNormalizationDoseValue, "DVHNormalizationDoseValue");
        ensureInAttributeMetadata(TagFromName.DVHNormalizationPoint, "DVHNormalizationPoint");
        ensureInAttributeMetadata(TagFromName.DVHNumberOfBins, "DVHNumberOfBins");
        ensureInAttributeMetadata(TagFromName.DVHReferencedROISequence, "DVHReferencedROISequence");
        ensureInAttributeMetadata(TagFromName.DVHROIContributionType, "DVHROIContributionType");
        ensureInAttributeMetadata(TagFromName.DVHSequence, "DVHSequence");
        ensureInAttributeMetadata(TagFromName.DVHType, "DVHType");
        ensureInAttributeMetadata(TagFromName.DVHVolumeUnits, "DVHVolumeUnits");
        ensureInAttributeMetadata(TagFromName.DynamicRange, "DynamicRange");
        ensureInAttributeMetadata(TagFromName.EchoNumber, "EchoNumber");
        ensureInAttributeMetadata(TagFromName.EchoPlanarPulseSequence, "EchoPlanarPulseSequence");
        ensureInAttributeMetadata(TagFromName.EchoPulseSequence, "EchoPulseSequence");
        ensureInAttributeMetadata(TagFromName.EchoTime, "EchoTime");
        ensureInAttributeMetadata(TagFromName.EchoTrainLength, "EchoTrainLength");
        ensureInAttributeMetadata(TagFromName.EffectiveDuration, "EffectiveDuration");
        ensureInAttributeMetadata(TagFromName.EffectiveEchoTime, "EffectiveEchoTime");
        ensureInAttributeMetadata(TagFromName.EmmetropicMagnification, "EmmetropicMagnification");
        ensureInAttributeMetadata(TagFromName.EmptyImageDensity, "EmptyImageDensity");
        ensureInAttributeMetadata(TagFromName.EncapsulatedDocument, "EncapsulatedDocument");
        ensureInAttributeMetadata(TagFromName.EncryptedAttributesSequence, "EncryptedAttributesSequence");
        ensureInAttributeMetadata(TagFromName.EncryptedContent, "EncryptedContent");
        ensureInAttributeMetadata(TagFromName.EncryptedContentTransferSyntaxUID, "EncryptedContentTransferSyntaxUID");
        ensureInAttributeMetadata(TagFromName.EndAcquisitionDateTime, "EndAcquisitionDateTime");
        ensureInAttributeMetadata(TagFromName.EndCumulativeMetersetWeight, "EndCumulativeMetersetWeight");
        ensureInAttributeMetadata(TagFromName.EndingRespiratoryAmplitude, "EndingRespiratoryAmplitude");
        ensureInAttributeMetadata(TagFromName.EndingRespiratoryPhase, "EndingRespiratoryPhase");
        ensureInAttributeMetadata(TagFromName.EndMessageSet, "EndMessageSet");
        ensureInAttributeMetadata(TagFromName.EndMeterset, "EndMeterset");
        ensureInAttributeMetadata(TagFromName.EnergyWindowCenterline, "EnergyWindowCenterline");
        ensureInAttributeMetadata(TagFromName.EnergyWindowInformationSequence, "EnergyWindowInformationSequence");
        ensureInAttributeMetadata(TagFromName.EnergyWindowLowerLimit, "EnergyWindowLowerLimit");
        ensureInAttributeMetadata(TagFromName.EnergyWindowName, "EnergyWindowName");
        ensureInAttributeMetadata(TagFromName.EnergyWindowNumber, "EnergyWindowNumber");
        ensureInAttributeMetadata(TagFromName.EnergyWindowRangeSequence, "EnergyWindowRangeSequence");
        ensureInAttributeMetadata(TagFromName.EnergyWindowTotalWidth, "EnergyWindowTotalWidth");
        ensureInAttributeMetadata(TagFromName.EnergyWindowUpperLimit, "EnergyWindowUpperLimit");
        ensureInAttributeMetadata(TagFromName.EnergyWindowVector, "EnergyWindowVector");
        ensureInAttributeMetadata(TagFromName.EntranceDose, "EntranceDose");
        ensureInAttributeMetadata(TagFromName.EntranceDoseInmGy, "EntranceDoseInmGy");
        ensureInAttributeMetadata(TagFromName.EquipmentCoordinateSystemIdentification,
                "EquipmentCoordinateSystemIdentification");
        ensureInAttributeMetadata(TagFromName.EquivalentCDADocumentSequence, "EquivalentCDADocumentSequence");
        ensureInAttributeMetadata(TagFromName.Erase, "Erase");
        ensureInAttributeMetadata(TagFromName.ErrorComment, "ErrorComment");
        ensureInAttributeMetadata(TagFromName.ErrorID, "ErrorID");
        ensureInAttributeMetadata(TagFromName.EscapeTriplet, "EscapeTriplet");
        ensureInAttributeMetadata(TagFromName.EstimatedDoseSaving, "EstimatedDoseSaving");
        ensureInAttributeMetadata(TagFromName.EstimatedRadiographicMagnificationFactor,
                "EstimatedRadiographicMagnificationFactor");
        ensureInAttributeMetadata(TagFromName.EthnicGroup, "EthnicGroup");
        ensureInAttributeMetadata(TagFromName.EventElapsedTime, "EventElapsedTime");
        ensureInAttributeMetadata(TagFromName.EventTimerName, "EventTimerName");
        ensureInAttributeMetadata(TagFromName.EventTypeID, "EventTypeID");
        ensureInAttributeMetadata(TagFromName.ExaminedBodyThickness, "ExaminedBodyThickness");
        ensureInAttributeMetadata(TagFromName.ExecutionStatus, "ExecutionStatus");
        ensureInAttributeMetadata(TagFromName.ExecutionStatusInfo, "ExecutionStatusInfo");
        ensureInAttributeMetadata(TagFromName.ExpectedCompletionDateAndTime, "ExpectedCompletionDateAndTime");
        ensureInAttributeMetadata(TagFromName.ExposedArea, "ExposedArea");
        ensureInAttributeMetadata(TagFromName.Exposure, "Exposure");
        ensureInAttributeMetadata(TagFromName.ExposureControlMode, "ExposureControlMode");
        ensureInAttributeMetadata(TagFromName.ExposureControlModeDescription, "ExposureControlModeDescription");
        ensureInAttributeMetadata(TagFromName.ExposureControlSensingRegionLeftVerticalEdge,
                "ExposureControlSensingRegionLeftVerticalEdge");
        ensureInAttributeMetadata(TagFromName.ExposureControlSensingRegionLowerHorizontalEdge,
                "ExposureControlSensingRegionLowerHorizontalEdge");
        ensureInAttributeMetadata(TagFromName.ExposureControlSensingRegionRightVerticalEdge,
                "ExposureControlSensingRegionRightVerticalEdge");
        ensureInAttributeMetadata(TagFromName.ExposureControlSensingRegionShape, "ExposureControlSensingRegionShape");
        ensureInAttributeMetadata(TagFromName.ExposureControlSensingRegionsSequence,
                "ExposureControlSensingRegionsSequence");
        ensureInAttributeMetadata(TagFromName.ExposureControlSensingRegionUpperHorizontalEdge,
                "ExposureControlSensingRegionUpperHorizontalEdge");
        ensureInAttributeMetadata(TagFromName.ExposureDoseSequence, "ExposureDoseSequence");
        ensureInAttributeMetadata(TagFromName.ExposureInmAs, "ExposureInmAs");
        ensureInAttributeMetadata(TagFromName.ExposureInuAs, "ExposureInuAs");
        ensureInAttributeMetadata(TagFromName.ExposureModulationType, "ExposureModulationType");
        ensureInAttributeMetadata(TagFromName.ExposureSequence, "ExposureSequence");
        ensureInAttributeMetadata(TagFromName.ExposuresOnDetectorSinceLastCalibration,
                "ExposuresOnDetectorSinceLastCalibration");
        ensureInAttributeMetadata(TagFromName.ExposuresOnDetectorSinceManufactured,
                "ExposuresOnDetectorSinceManufactured");
        ensureInAttributeMetadata(TagFromName.ExposuresOnPlate, "ExposuresOnPlate");
        ensureInAttributeMetadata(TagFromName.ExposureStatus, "ExposureStatus");
        ensureInAttributeMetadata(TagFromName.ExposureTime, "ExposureTime");
        ensureInAttributeMetadata(TagFromName.ExposureTimeInms, "ExposureTimeInms");
        ensureInAttributeMetadata(TagFromName.ExposureTimeInuS, "ExposureTimeInuS");
        ensureInAttributeMetadata(TagFromName.FailedSOPInstanceUIDList, "FailedSOPInstanceUIDList");
        ensureInAttributeMetadata(TagFromName.FailedSOPSequence, "FailedSOPSequence");
        ensureInAttributeMetadata(TagFromName.FailureAttributes, "FailureAttributes");
        ensureInAttributeMetadata(TagFromName.FailureReason, "FailureReason");
        ensureInAttributeMetadata(TagFromName.FiducialDescription, "FiducialDescription");
        ensureInAttributeMetadata(TagFromName.FiducialIdentifier, "FiducialIdentifier");
        ensureInAttributeMetadata(TagFromName.FiducialIdentifierCodeSequence, "FiducialIdentifierCodeSequence");
        ensureInAttributeMetadata(TagFromName.FiducialSequence, "FiducialSequence");
        ensureInAttributeMetadata(TagFromName.FiducialSetSequence, "FiducialSetSequence");
        ensureInAttributeMetadata(TagFromName.FiducialUID, "FiducialUID");
        ensureInAttributeMetadata(TagFromName.FieldOfViewDescription, "FieldOfViewDescription");
        ensureInAttributeMetadata(TagFromName.FieldOfViewDimensions, "FieldOfViewDimensions");
        ensureInAttributeMetadata(TagFromName.FieldOfViewDimensionsInFloat, "FieldOfViewDimensionsInFloat");
        ensureInAttributeMetadata(TagFromName.FieldOfViewHorizontalFlip, "FieldOfViewHorizontalFlip");
        ensureInAttributeMetadata(TagFromName.FieldOfViewOrigin, "FieldOfViewOrigin");
        ensureInAttributeMetadata(TagFromName.FieldOfViewRotation, "FieldOfViewRotation");
        ensureInAttributeMetadata(TagFromName.FieldOfViewSequence, "FieldOfViewSequence");
        ensureInAttributeMetadata(TagFromName.FieldOfViewShape, "FieldOfViewShape");
        ensureInAttributeMetadata(TagFromName.FileMetaInformationGroupLength, "FileMetaInformationGroupLength");
        ensureInAttributeMetadata(TagFromName.FileMetaInformationVersion, "FileMetaInformationVersion");
        ensureInAttributeMetadata(TagFromName.FileSetCharacterSet, "FileSetCharacterSet");
        ensureInAttributeMetadata(TagFromName.FileSetConsistencyFlag, "FileSetConsistencyFlag");
        ensureInAttributeMetadata(TagFromName.FileSetDescriptorFileID, "FileSetDescriptorFileID");
        ensureInAttributeMetadata(TagFromName.FileSetID, "FileSetID");
        ensureInAttributeMetadata(TagFromName.FillerOrderNumberOfImagingServiceRequest,
                "FillerOrderNumberOfImagingServiceRequest");
        ensureInAttributeMetadata(TagFromName.FillerOrderNumberOfImagingServiceRequestRetired,
                "FillerOrderNumberOfImagingServiceRequestRetired");
        ensureInAttributeMetadata(TagFromName.FillerOrderNumberOfProcedure, "FillerOrderNumberOfProcedure");
        ensureInAttributeMetadata(TagFromName.FilmBoxContentSequence, "FilmBoxContentSequence");
        ensureInAttributeMetadata(TagFromName.FilmConsumptionSequence, "FilmConsumptionSequence");
        ensureInAttributeMetadata(TagFromName.FilmDestination, "FilmDestination");
        ensureInAttributeMetadata(TagFromName.FilmOrientation, "FilmOrientation");
        ensureInAttributeMetadata(TagFromName.FilmSessionLabel, "FilmSessionLabel");
        ensureInAttributeMetadata(TagFromName.FilmSizeID, "FilmSizeID");
        ensureInAttributeMetadata(TagFromName.FilterByAttributePresence, "FilterByAttributePresence");
        ensureInAttributeMetadata(TagFromName.FilterByCategory, "FilterByCategory");
        ensureInAttributeMetadata(TagFromName.FilterByOperator, "FilterByOperator");
        ensureInAttributeMetadata(TagFromName.FilterHighFrequency, "FilterHighFrequency");
        ensureInAttributeMetadata(TagFromName.FilterLowFrequency, "FilterLowFrequency");
        ensureInAttributeMetadata(TagFromName.FilterMaterial, "FilterMaterial");
        ensureInAttributeMetadata(TagFromName.FilterOperationsSequence, "FilterOperationsSequence");
        ensureInAttributeMetadata(TagFromName.FilterThicknessMaximum, "FilterThicknessMaximum");
        ensureInAttributeMetadata(TagFromName.FilterThicknessMinimum, "FilterThicknessMinimum");
        ensureInAttributeMetadata(TagFromName.FilterType, "FilterType");
        ensureInAttributeMetadata(TagFromName.FinalCumulativeMetersetWeight, "FinalCumulativeMetersetWeight");
        ensureInAttributeMetadata(TagFromName.FinalCumulativeTimeWeight, "FinalCumulativeTimeWeight");
        // ensureInAttributeMetadata(TagFromName.FindingsFlagTrial,
        // "FindingsFlagTrial");
        // ensureInAttributeMetadata(TagFromName.FindingsGroupRecordingDateTrial,
        // "FindingsGroupRecordingDateTrial");
        // ensureInAttributeMetadata(TagFromName.FindingsGroupRecordingTimeTrial,
        // "FindingsGroupRecordingTimeTrial");
        // ensureInAttributeMetadata(TagFromName.FindingsGroupUIDTrial,
        // "FindingsGroupUIDTrial");
        // ensureInAttributeMetadata(TagFromName.FindingsSequenceTrial,
        // "FindingsSequenceTrial");
        // ensureInAttributeMetadata(TagFromName.FindingsSourceCategoryCodeSequenceTrial,
        // "FindingsSourceCategoryCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.FindLocation, "FindLocation");
        ensureInAttributeMetadata(TagFromName.FirstOrderPhaseCorrection, "FirstOrderPhaseCorrection");
        ensureInAttributeMetadata(TagFromName.FirstOrderPhaseCorrectionAngle, "FirstOrderPhaseCorrectionAngle");
        ensureInAttributeMetadata(TagFromName.FirstTreatmentDate, "FirstTreatmentDate");
        ensureInAttributeMetadata(TagFromName.FixationDeviceDescription, "FixationDeviceDescription");
        ensureInAttributeMetadata(TagFromName.FixationDeviceLabel, "FixationDeviceLabel");
        ensureInAttributeMetadata(TagFromName.FixationDevicePitchAngle, "FixationDevicePitchAngle");
        ensureInAttributeMetadata(TagFromName.FixationDevicePosition, "FixationDevicePosition");
        ensureInAttributeMetadata(TagFromName.FixationDeviceRollAngle, "FixationDeviceRollAngle");
        ensureInAttributeMetadata(TagFromName.FixationDeviceSequence, "FixationDeviceSequence");
        ensureInAttributeMetadata(TagFromName.FixationDeviceType, "FixationDeviceType");
        ensureInAttributeMetadata(TagFromName.FixationLightAzimuthalAngle, "FixationLightAzimuthalAngle");
        ensureInAttributeMetadata(TagFromName.FixationLightPolarAngle, "FixationLightPolarAngle");
        ensureInAttributeMetadata(TagFromName.FlipAngle, "FlipAngle");
        ensureInAttributeMetadata(TagFromName.FlowCompensation, "FlowCompensation");
        ensureInAttributeMetadata(TagFromName.FlowCompensationDirection, "FlowCompensationDirection");
        ensureInAttributeMetadata(TagFromName.FluenceDataScale, "FluenceDataScale");
        ensureInAttributeMetadata(TagFromName.FluenceDataSource, "FluenceDataSource");
        ensureInAttributeMetadata(TagFromName.FluenceMapSequence, "FluenceMapSequence");
        ensureInAttributeMetadata(TagFromName.FluoroscopyFlag, "FluoroscopyFlag");
        ensureInAttributeMetadata(TagFromName.FocalDistance, "FocalDistance");
        ensureInAttributeMetadata(TagFromName.FocalSpot, "FocalSpot");
        ensureInAttributeMetadata(TagFromName.FocusDepth, "FocusDepth");
        ensureInAttributeMetadata(TagFromName.FractionalChannelDisplayScale, "FractionalChannelDisplayScale");
        ensureInAttributeMetadata(TagFromName.FractionGroupDescription, "FractionGroupDescription");
        ensureInAttributeMetadata(TagFromName.FractionGroupNumber, "FractionGroupNumber");
        ensureInAttributeMetadata(TagFromName.FractionGroupSequence, "FractionGroupSequence");
        ensureInAttributeMetadata(TagFromName.FractionGroupSummarySequence, "FractionGroupSummarySequence");
        ensureInAttributeMetadata(TagFromName.FractionGroupType, "FractionGroupType");
        ensureInAttributeMetadata(TagFromName.FractionNumber, "FractionNumber");
        ensureInAttributeMetadata(TagFromName.FractionPattern, "FractionPattern");
        ensureInAttributeMetadata(TagFromName.FractionStatusSummarySequence, "FractionStatusSummarySequence");
        ensureInAttributeMetadata(TagFromName.FrameAcquisitionDateTime, "FrameAcquisitionDateTime");
        ensureInAttributeMetadata(TagFromName.FrameAcquisitionDuration, "FrameAcquisitionDuration");
        ensureInAttributeMetadata(TagFromName.FrameAcquisitionNumber, "FrameAcquisitionNumber");
        ensureInAttributeMetadata(TagFromName.FrameAcquisitionSequence, "FrameAcquisitionSequence");
        ensureInAttributeMetadata(TagFromName.FrameAnatomySequence, "FrameAnatomySequence");
        ensureInAttributeMetadata(TagFromName.FrameComments, "FrameComments");
        ensureInAttributeMetadata(TagFromName.FrameContentSequence, "FrameContentSequence");
        ensureInAttributeMetadata(TagFromName.FrameDelay, "FrameDelay");
        ensureInAttributeMetadata(TagFromName.FrameDetectorParametersSequence, "FrameDetectorParametersSequence");
        ensureInAttributeMetadata(TagFromName.FrameDimensionPointer, "FrameDimensionPointer");
        ensureInAttributeMetadata(TagFromName.FrameDisplaySequence, "FrameDisplaySequence");
        ensureInAttributeMetadata(TagFromName.FrameDisplayShutterSequence, "FrameDisplayShutterSequence");
        ensureInAttributeMetadata(TagFromName.FrameIncrementPointer, "FrameIncrementPointer");
        ensureInAttributeMetadata(TagFromName.FrameLabel, "FrameLabel");
        ensureInAttributeMetadata(TagFromName.FrameLabelVector, "FrameLabelVector");
        ensureInAttributeMetadata(TagFromName.FrameLaterality, "FrameLaterality");
        ensureInAttributeMetadata(TagFromName.FrameNumbersOfInterest, "FrameNumbersOfInterest");
        ensureInAttributeMetadata(TagFromName.FrameOfInterestDescription, "FrameOfInterestDescription");
        ensureInAttributeMetadata(TagFromName.FrameOfInterestType, "FrameOfInterestType");
        ensureInAttributeMetadata(TagFromName.FrameOfReferenceRelationshipSequence,
                "FrameOfReferenceRelationshipSequence");
        ensureInAttributeMetadata(TagFromName.FrameOfReferenceTransformationComment,
                "FrameOfReferenceTransformationComment");
        ensureInAttributeMetadata(TagFromName.FrameOfReferenceTransformationMatrix,
                "FrameOfReferenceTransformationMatrix");
        ensureInAttributeMetadata(TagFromName.FrameOfReferenceTransformationMatrixType,
                "FrameOfReferenceTransformationMatrixType");
        ensureInAttributeMetadata(TagFromName.FrameOfReferenceTransformationType, "FrameOfReferenceTransformationType");
        ensureInAttributeMetadata(TagFromName.FrameOfReferenceUID, "FrameOfReferenceUID");
        ensureInAttributeMetadata(TagFromName.FramePixelDataPropertiesSequence, "FramePixelDataPropertiesSequence");
        ensureInAttributeMetadata(TagFromName.FramePixelShiftSequence, "FramePixelShiftSequence");
        ensureInAttributeMetadata(TagFromName.FramePrimaryAngleVector, "FramePrimaryAngleVector");
        ensureInAttributeMetadata(TagFromName.FrameReferenceDateTime, "FrameReferenceDateTime");
        ensureInAttributeMetadata(TagFromName.FrameReferenceTime, "FrameReferenceTime");
        ensureInAttributeMetadata(TagFromName.FrameSecondaryAngleVector, "FrameSecondaryAngleVector");
        ensureInAttributeMetadata(TagFromName.FrameTime, "FrameTime");
        ensureInAttributeMetadata(TagFromName.FrameTimeVector, "FrameTimeVector");
        ensureInAttributeMetadata(TagFromName.FrameType, "FrameType");
        ensureInAttributeMetadata(TagFromName.FrameVOILUTSequence, "FrameVOILUTSequence");
        ensureInAttributeMetadata(TagFromName.FrequencyCorrection, "FrequencyCorrection");
        ensureInAttributeMetadata(TagFromName.FunctionalGroupPointer, "FunctionalGroupPointer");
        ensureInAttributeMetadata(TagFromName.FunctionalGroupPrivateCreator, "FunctionalGroupPrivateCreator");
        ensureInAttributeMetadata(TagFromName.GantryAngle, "GantryAngle");
        ensureInAttributeMetadata(TagFromName.GantryAngleTolerance, "GantryAngleTolerance");
        ensureInAttributeMetadata(TagFromName.GantryDetectorSlew, "GantryDetectorSlew");
        ensureInAttributeMetadata(TagFromName.GantryDetectorTilt, "GantryDetectorTilt");
        ensureInAttributeMetadata(TagFromName.GantryID, "GantryID");
        ensureInAttributeMetadata(TagFromName.GantryPitchAngle, "GantryPitchAngle");
        ensureInAttributeMetadata(TagFromName.GantryPitchAngleTolerance, "GantryPitchAngleTolerance");
        ensureInAttributeMetadata(TagFromName.GantryPitchRotationDirection, "GantryPitchRotationDirection");
        ensureInAttributeMetadata(TagFromName.GantryRotationDirection, "GantryRotationDirection");
        ensureInAttributeMetadata(TagFromName.GatedInformationSequence, "GatedInformationSequence");
        ensureInAttributeMetadata(TagFromName.GeneralAccessoryDescription, "GeneralAccessoryDescription");
        ensureInAttributeMetadata(TagFromName.GeneralAccessoryID, "GeneralAccessoryID");
        ensureInAttributeMetadata(TagFromName.GeneralAccessoryNumber, "GeneralAccessoryNumber");
        ensureInAttributeMetadata(TagFromName.GeneralAccessorySequence, "GeneralAccessorySequence");
        ensureInAttributeMetadata(TagFromName.GeneralAccessoryType, "GeneralAccessoryType");
        ensureInAttributeMetadata(TagFromName.GeneralPurposePerformedProcedureStepStatus,
                "GeneralPurposePerformedProcedureStepStatus");
        ensureInAttributeMetadata(TagFromName.GeneralPurposeScheduledProcedureStepPriority,
                "GeneralPurposeScheduledProcedureStepPriority");
        ensureInAttributeMetadata(TagFromName.GeneralPurposeScheduledProcedureStepStatus,
                "GeneralPurposeScheduledProcedureStepStatus");
        ensureInAttributeMetadata(TagFromName.GeneratorID, "GeneratorID");
        ensureInAttributeMetadata(TagFromName.GeneratorPower, "GeneratorPower");
        ensureInAttributeMetadata(TagFromName.GeometricalProperties, "GeometricalProperties");
        ensureInAttributeMetadata(TagFromName.GeometricMaximumDistortion, "GeometricMaximumDistortion");
        ensureInAttributeMetadata(TagFromName.GeometryOfKSpaceTraversal, "GeometryOfKSpaceTraversal");
        ensureInAttributeMetadata(TagFromName.GradientEchoTrainLength, "GradientEchoTrainLength");
        ensureInAttributeMetadata(TagFromName.GradientOutput, "GradientOutput");
        ensureInAttributeMetadata(TagFromName.GradientOutputType, "GradientOutputType");
        ensureInAttributeMetadata(TagFromName.GraphicAnnotationSequence, "GraphicAnnotationSequence");
        ensureInAttributeMetadata(TagFromName.GraphicAnnotationUnits, "GraphicAnnotationUnits");
        ensureInAttributeMetadata(TagFromName.GraphicCoordinatesDataSequence, "GraphicCoordinatesDataSequence");
        ensureInAttributeMetadata(TagFromName.GraphicData, "GraphicData");
        ensureInAttributeMetadata(TagFromName.GraphicDimensions, "GraphicDimensions");
        ensureInAttributeMetadata(TagFromName.GraphicFilled, "GraphicFilled");
        ensureInAttributeMetadata(TagFromName.GraphicLayer, "GraphicLayer");
        ensureInAttributeMetadata(TagFromName.GraphicLayerDescription, "GraphicLayerDescription");
        ensureInAttributeMetadata(TagFromName.GraphicLayerOrder, "GraphicLayerOrder");
        ensureInAttributeMetadata(TagFromName.GraphicLayerRecommendedDisplayCIELabValue,
                "GraphicLayerRecommendedDisplayCIELabValue");
        ensureInAttributeMetadata(TagFromName.GraphicLayerRecommendedDisplayGrayscaleValue,
                "GraphicLayerRecommendedDisplayGrayscaleValue");
        ensureInAttributeMetadata(TagFromName.GraphicLayerRecommendedDisplayRGBValue,
                "GraphicLayerRecommendedDisplayRGBValue");
        ensureInAttributeMetadata(TagFromName.GraphicLayerSequence, "GraphicLayerSequence");
        ensureInAttributeMetadata(TagFromName.GraphicObjectSequence, "GraphicObjectSequence");
        ensureInAttributeMetadata(TagFromName.GraphicType, "GraphicType");
        ensureInAttributeMetadata(TagFromName.GrayLookupTableData, "GrayLookupTableData");
        ensureInAttributeMetadata(TagFromName.GrayLookupTableDescriptor, "GrayLookupTableDescriptor");
        ensureInAttributeMetadata(TagFromName.GrayScale, "GrayScale");
        ensureInAttributeMetadata(TagFromName.GreenPaletteColorLookupTableData, "GreenPaletteColorLookupTableData");
        ensureInAttributeMetadata(TagFromName.GreenPaletteColorLookupTableDescriptor,
                "GreenPaletteColorLookupTableDescriptor");
        ensureInAttributeMetadata(TagFromName.Grid, "Grid");
        ensureInAttributeMetadata(TagFromName.GridAbsorbingMaterial, "GridAbsorbingMaterial");
        ensureInAttributeMetadata(TagFromName.GridAspectRatio, "GridAspectRatio");
        ensureInAttributeMetadata(TagFromName.GridDimensions, "GridDimensions");
        ensureInAttributeMetadata(TagFromName.GridFocalDistance, "GridFocalDistance");
        ensureInAttributeMetadata(TagFromName.GridFrameOffsetVector, "GridFrameOffsetVector");
        ensureInAttributeMetadata(TagFromName.GridID, "GridID");
        ensureInAttributeMetadata(TagFromName.GridPeriod, "GridPeriod");
        ensureInAttributeMetadata(TagFromName.GridPitch, "GridPitch");
        ensureInAttributeMetadata(TagFromName.GridResolution, "GridResolution");
        ensureInAttributeMetadata(TagFromName.GridSpacingMaterial, "GridSpacingMaterial");
        ensureInAttributeMetadata(TagFromName.GridThickness, "GridThickness");
        ensureInAttributeMetadata(TagFromName.HalfValueLayer, "HalfValueLayer");
        ensureInAttributeMetadata(TagFromName.HangingProtocolCreationDateTime, "HangingProtocolCreationDateTime");
        ensureInAttributeMetadata(TagFromName.HangingProtocolCreator, "HangingProtocolCreator");
        ensureInAttributeMetadata(TagFromName.HangingProtocolDefinitionSequence, "HangingProtocolDefinitionSequence");
        ensureInAttributeMetadata(TagFromName.HangingProtocolDescription, "HangingProtocolDescription");
        ensureInAttributeMetadata(TagFromName.HangingProtocolLevel, "HangingProtocolLevel");
        ensureInAttributeMetadata(TagFromName.HangingProtocolName, "HangingProtocolName");
        ensureInAttributeMetadata(TagFromName.HangingProtocolUserGroupName, "HangingProtocolUserGroupName");
        ensureInAttributeMetadata(TagFromName.HangingProtocolUserIdentificationCodeSequence,
                "HangingProtocolUserIdentificationCodeSequence");
        ensureInAttributeMetadata(TagFromName.HardcopyCreationDeviceID, "HardcopyCreationDeviceID");
        ensureInAttributeMetadata(TagFromName.HardcopyDeviceManufacturer, "HardcopyDeviceManufacturer");
        ensureInAttributeMetadata(TagFromName.HardcopyDeviceManufacturerModelName,
                "HardcopyDeviceManufacturerModelName");
        ensureInAttributeMetadata(TagFromName.HardcopyDeviceSoftwareVersion, "HardcopyDeviceSoftwareVersion");
        ensureInAttributeMetadata(TagFromName.HeadFixationAngle, "HeadFixationAngle");
        ensureInAttributeMetadata(TagFromName.HeartRate, "HeartRate");
        ensureInAttributeMetadata(TagFromName.HighBit, "HighBit");
        ensureInAttributeMetadata(TagFromName.HighDoseTechniqueType, "HighDoseTechniqueType");
        ensureInAttributeMetadata(TagFromName.HighRRValue, "HighRRValue");
        ensureInAttributeMetadata(TagFromName.HistogramBinWidth, "HistogramBinWidth");
        ensureInAttributeMetadata(TagFromName.HistogramData, "HistogramData");
        ensureInAttributeMetadata(TagFromName.HistogramExplanation, "HistogramExplanation");
        ensureInAttributeMetadata(TagFromName.HistogramFirstBinValue, "HistogramFirstBinValue");
        ensureInAttributeMetadata(TagFromName.HistogramLastBinValue, "HistogramLastBinValue");
        ensureInAttributeMetadata(TagFromName.HistogramNumberOfBins, "HistogramNumberOfBins");
        ensureInAttributeMetadata(TagFromName.HistogramSequence, "HistogramSequence");
        ensureInAttributeMetadata(TagFromName.HL7DocumentEffectiveTime, "HL7DocumentEffectiveTime");
        ensureInAttributeMetadata(TagFromName.HL7DocumentTypeCodeSequence, "HL7DocumentTypeCodeSequence");
        ensureInAttributeMetadata(TagFromName.HL7InstanceIdentifier, "HL7InstanceIdentifier");
        ensureInAttributeMetadata(TagFromName.HL7StructuredDocumentReferenceSequence,
                "HL7StructuredDocumentReferenceSequence");
        ensureInAttributeMetadata(TagFromName.HorizontalFieldOfView, "HorizontalFieldOfView");
        ensureInAttributeMetadata(TagFromName.HuffmanTableSize, "HuffmanTableSize");
        ensureInAttributeMetadata(TagFromName.HuffmanTableTriplet, "HuffmanTableTriplet");
        ensureInAttributeMetadata(TagFromName.HumanPerformerCodeSequence, "HumanPerformerCodeSequence");
        ensureInAttributeMetadata(TagFromName.HumanPerformersName, "HumanPerformersName");
        ensureInAttributeMetadata(TagFromName.HumanPerformersOrganization, "HumanPerformersOrganization");
        ensureInAttributeMetadata(TagFromName.ICCProfile, "ICCProfile");
        ensureInAttributeMetadata(TagFromName.IconImageSequence, "IconImageSequence");
        ensureInAttributeMetadata(TagFromName.IdenticalDocumentsSequence, "IdenticalDocumentsSequence");
        // ensureInAttributeMetadata(TagFromName.IdentificationDescriptionTrial,
        // "IdentificationDescriptionTrial");
        // ensureInAttributeMetadata(TagFromName.IdentifierCodeSequenceTrial,
        // "IdentifierCodeSequenceTrial");
        // ensureInAttributeMetadata(TagFromName.IdentifierTypeCode,
        // "IdentifierTypeCode");
        ensureInAttributeMetadata(TagFromName.IdentifyingComments, "IdentifyingComments");
        ensureInAttributeMetadata(TagFromName.Illumination, "Illumination");
        ensureInAttributeMetadata(TagFromName.IlluminationBandwidth, "IlluminationBandwidth");
        ensureInAttributeMetadata(TagFromName.IlluminationPower, "IlluminationPower");
        ensureInAttributeMetadata(TagFromName.IlluminationTypeCodeSequence, "IlluminationTypeCodeSequence");
        ensureInAttributeMetadata(TagFromName.IlluminationWaveLength, "IlluminationWaveLength");
        ensureInAttributeMetadata(TagFromName.ImageAndFluoroscopyAreaDoseProduct, "ImageAndFluoroscopyAreaDoseProduct");
        ensureInAttributeMetadata(TagFromName.ImageBoxContentSequence, "ImageBoxContentSequence");
        ensureInAttributeMetadata(TagFromName.ImageBoxesSequence, "ImageBoxesSequence");
        ensureInAttributeMetadata(TagFromName.ImageBoxLargeScrollAmount, "ImageBoxLargeScrollAmount");
        ensureInAttributeMetadata(TagFromName.ImageBoxLargeScrollType, "ImageBoxLargeScrollType");
        ensureInAttributeMetadata(TagFromName.ImageBoxLayoutType, "ImageBoxLayoutType");
        ensureInAttributeMetadata(TagFromName.ImageBoxNumber, "ImageBoxNumber");
        ensureInAttributeMetadata(TagFromName.ImageBoxOverlapPriority, "ImageBoxOverlapPriority");
        ensureInAttributeMetadata(TagFromName.ImageBoxPosition, "ImageBoxPosition");
        ensureInAttributeMetadata(TagFromName.ImageBoxPresentationLUTFlag, "ImageBoxPresentationLUTFlag");
        ensureInAttributeMetadata(TagFromName.ImageBoxScrollDirection, "ImageBoxScrollDirection");
        ensureInAttributeMetadata(TagFromName.ImageBoxSmallScrollAmount, "ImageBoxSmallScrollAmount");
        ensureInAttributeMetadata(TagFromName.ImageBoxSmallScrollType, "ImageBoxSmallScrollType");
        ensureInAttributeMetadata(TagFromName.ImageBoxTileHorizontalDimension, "ImageBoxTileHorizontalDimension");
        ensureInAttributeMetadata(TagFromName.ImageBoxTileVerticalDimension, "ImageBoxTileVerticalDimension");
        ensureInAttributeMetadata(TagFromName.ImageCenterPointCoordinatesSequence,
                "ImageCenterPointCoordinatesSequence");
        ensureInAttributeMetadata(TagFromName.ImageComments, "ImageComments");
        ensureInAttributeMetadata(TagFromName.ImageDataLocation, "ImageDataLocation");
        ensureInAttributeMetadata(TagFromName.ImageDimensions, "ImageDimensions");
        ensureInAttributeMetadata(TagFromName.ImageDisplayFormat, "ImageDisplayFormat");
        ensureInAttributeMetadata(TagFromName.ImagedNucleus, "ImagedNucleus");
        ensureInAttributeMetadata(TagFromName.ImageFilter, "ImageFilter");
        ensureInAttributeMetadata(TagFromName.ImageFormat, "ImageFormat");
        ensureInAttributeMetadata(TagFromName.ImageFrameOrigin, "ImageFrameOrigin");
        ensureInAttributeMetadata(TagFromName.ImageGeometryType, "ImageGeometryType");
        ensureInAttributeMetadata(TagFromName.ImageHorizontalFlip, "ImageHorizontalFlip");
        ensureInAttributeMetadata(TagFromName.ImageID, "ImageID");
        ensureInAttributeMetadata(TagFromName.ImageIndex, "ImageIndex");
        ensureInAttributeMetadata(TagFromName.ImageLaterality, "ImageLaterality");
        ensureInAttributeMetadata(TagFromName.ImageLocation, "ImageLocation");
        ensureInAttributeMetadata(TagFromName.ImageOrientation, "ImageOrientation");
        ensureInAttributeMetadata(TagFromName.ImageOrientationPatient, "ImageOrientationPatient");
        ensureInAttributeMetadata(TagFromName.ImageOverlayBoxContentSequence, "ImageOverlayBoxContentSequence");
        ensureInAttributeMetadata(TagFromName.ImageOverlayFlag, "ImageOverlayFlag");
        ensureInAttributeMetadata(TagFromName.ImagePathFilterPassBand, "ImagePathFilterPassBand");
        ensureInAttributeMetadata(TagFromName.ImagePathFilterPassThroughWavelength,
                "ImagePathFilterPassThroughWavelength");
        ensureInAttributeMetadata(TagFromName.ImagePathFilterTypeStackCodeSequence,
                "ImagePathFilterTypeStackCodeSequence");
        ensureInAttributeMetadata(TagFromName.ImagePlanePixelSpacing, "ImagePlanePixelSpacing");
        ensureInAttributeMetadata(TagFromName.ImagePosition, "ImagePosition");
        ensureInAttributeMetadata(TagFromName.ImagePositionPatient, "ImagePositionPatient");
        ensureInAttributeMetadata(TagFromName.ImagePresentationComments, "ImagePresentationComments");
        ensureInAttributeMetadata(TagFromName.ImageProcessingApplied, "ImageProcessingApplied");
        ensureInAttributeMetadata(TagFromName.ImageRotation, "ImageRotation");
        ensureInAttributeMetadata(TagFromName.ImageRotationTrial, "ImageRotationTrial");
        ensureInAttributeMetadata(TagFromName.ImagerPixelSpacing, "ImagerPixelSpacing");
        ensureInAttributeMetadata(TagFromName.ImageSetLabel, "ImageSetLabel");
        ensureInAttributeMetadata(TagFromName.ImageSetNumber, "ImageSetNumber");
        ensureInAttributeMetadata(TagFromName.ImageSetSelectorCategory, "ImageSetSelectorCategory");
        ensureInAttributeMetadata(TagFromName.ImageSetSelectorSequence, "ImageSetSelectorSequence");
        ensureInAttributeMetadata(TagFromName.ImageSetSelectorUsageFlag, "ImageSetSelectorUsageFlag");
        ensureInAttributeMetadata(TagFromName.ImageSetsSequence, "ImageSetsSequence");
        ensureInAttributeMetadata(TagFromName.ImagesInAcquisition, "ImagesInAcquisition");
        ensureInAttributeMetadata(TagFromName.ImagesInSeries, "ImagesInSeries");
        ensureInAttributeMetadata(TagFromName.ImagesInStudy, "ImagesInStudy");
        ensureInAttributeMetadata(TagFromName.ImageToEquipmentMappingMatrix, "ImageToEquipmentMappingMatrix");
        ensureInAttributeMetadata(TagFromName.ImageTransformationMatrix, "ImageTransformationMatrix");
        ensureInAttributeMetadata(TagFromName.ImageTranslationVector, "ImageTranslationVector");
        ensureInAttributeMetadata(TagFromName.ImageTriggerDelay, "ImageTriggerDelay");
        ensureInAttributeMetadata(TagFromName.ImageType, "ImageType");
        ensureInAttributeMetadata(TagFromName.ImagingDeviceSpecificAcquisitionParameters,
                "ImagingDeviceSpecificAcquisitionParameters");
        ensureInAttributeMetadata(TagFromName.ImagingFrequency, "ImagingFrequency");
        ensureInAttributeMetadata(TagFromName.ImagingServiceRequestComments, "ImagingServiceRequestComments");
        ensureInAttributeMetadata(TagFromName.ImplantPresent, "ImplantPresent");
        ensureInAttributeMetadata(TagFromName.ImplementationClassUID, "ImplementationClassUID");
        ensureInAttributeMetadata(TagFromName.ImplementationVersionName, "ImplementationVersionName");
        ensureInAttributeMetadata(TagFromName.Impressions, "Impressions");
        ensureInAttributeMetadata(TagFromName.IncludeDisplayApplication, "IncludeDisplayApplication");
        ensureInAttributeMetadata(TagFromName.IncludeNonDICOMObjects, "IncludeNonDICOMObjects");
        ensureInAttributeMetadata(TagFromName.InConcatenationNumber, "InConcatenationNumber");
        ensureInAttributeMetadata(TagFromName.InConcatenationTotalNumber, "InConcatenationTotalNumber");
        ensureInAttributeMetadata(TagFromName.Initiator, "Initiator");
        ensureInAttributeMetadata(TagFromName.InPlanePhaseEncodingDirection, "InPlanePhaseEncodingDirection");
        ensureInAttributeMetadata(TagFromName.InputAvailabilityFlag, "InputAvailabilityFlag");
        ensureInAttributeMetadata(TagFromName.InputInformationSequence, "InputInformationSequence");
        ensureInAttributeMetadata(TagFromName.InStackPositionNumber, "InStackPositionNumber");
        ensureInAttributeMetadata(TagFromName.InstanceAvailability, "InstanceAvailability");
        ensureInAttributeMetadata(TagFromName.InstanceCreationDate, "InstanceCreationDate");
        ensureInAttributeMetadata(TagFromName.InstanceCreationTime, "InstanceCreationTime");
        ensureInAttributeMetadata(TagFromName.InstanceCreatorUID, "InstanceCreatorUID");
        ensureInAttributeMetadata(TagFromName.InstanceNumber, "InstanceNumber");
        ensureInAttributeMetadata(TagFromName.InstitutionAddress, "InstitutionAddress");
        ensureInAttributeMetadata(TagFromName.InstitutionalDepartmentName, "InstitutionalDepartmentName");
        ensureInAttributeMetadata(TagFromName.InstitutionCodeSequence, "InstitutionCodeSequence");
        ensureInAttributeMetadata(TagFromName.InstitutionName, "InstitutionName");
        ensureInAttributeMetadata(TagFromName.InsurancePlanIdentification, "InsurancePlanIdentification");
        ensureInAttributeMetadata(TagFromName.IntendedRecipientsOfResultsIdentificationSequence,
                "IntendedRecipientsOfResultsIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.IntensifierActiveDimensions, "IntensifierActiveDimensions");
        ensureInAttributeMetadata(TagFromName.IntensifierActiveShape, "IntensifierActiveShape");
        ensureInAttributeMetadata(TagFromName.IntensifierSize, "IntensifierSize");
        ensureInAttributeMetadata(TagFromName.InterMarkerDistance, "InterMarkerDistance");
        ensureInAttributeMetadata(TagFromName.InterpretationApprovalDate, "InterpretationApprovalDate");
        ensureInAttributeMetadata(TagFromName.InterpretationApprovalTime, "InterpretationApprovalTime");
        ensureInAttributeMetadata(TagFromName.InterpretationApproverSequence, "InterpretationApproverSequence");
        ensureInAttributeMetadata(TagFromName.InterpretationAuthor, "InterpretationAuthor");
        ensureInAttributeMetadata(TagFromName.InterpretationDiagnosisCodeSequence,
                "InterpretationDiagnosisCodeSequence");
        ensureInAttributeMetadata(TagFromName.InterpretationDiagnosisDescription, "InterpretationDiagnosisDescription");
        ensureInAttributeMetadata(TagFromName.InterpretationID, "InterpretationID");
        ensureInAttributeMetadata(TagFromName.InterpretationIDIssuer, "InterpretationIDIssuer");
        ensureInAttributeMetadata(TagFromName.InterpretationRecordedDate, "InterpretationRecordedDate");
        ensureInAttributeMetadata(TagFromName.InterpretationRecordedTime, "InterpretationRecordedTime");
        ensureInAttributeMetadata(TagFromName.InterpretationRecorder, "InterpretationRecorder");
        ensureInAttributeMetadata(TagFromName.InterpretationStatusID, "InterpretationStatusID");
        ensureInAttributeMetadata(TagFromName.InterpretationText, "InterpretationText");
        ensureInAttributeMetadata(TagFromName.InterpretationTranscriber, "InterpretationTranscriber");
        ensureInAttributeMetadata(TagFromName.InterpretationTranscriptionDate, "InterpretationTranscriptionDate");
        ensureInAttributeMetadata(TagFromName.InterpretationTranscriptionTime, "InterpretationTranscriptionTime");
        ensureInAttributeMetadata(TagFromName.InterpretationTypeID, "InterpretationTypeID");
        ensureInAttributeMetadata(TagFromName.IntervalNumber, "IntervalNumber");
        ensureInAttributeMetadata(TagFromName.IntervalsAcquired, "IntervalsAcquired");
        ensureInAttributeMetadata(TagFromName.IntervalsRejected, "IntervalsRejected");
        ensureInAttributeMetadata(TagFromName.InterventionDescription, "InterventionDescription");
        ensureInAttributeMetadata(TagFromName.InterventionDrugCodeSequence, "InterventionDrugCodeSequence");
        ensureInAttributeMetadata(TagFromName.InterventionDrugDose, "InterventionDrugDose");
        ensureInAttributeMetadata(TagFromName.InterventionDrugInformationSequence,
                "InterventionDrugInformationSequence");
        ensureInAttributeMetadata(TagFromName.InterventionDrugName, "InterventionDrugName");
        ensureInAttributeMetadata(TagFromName.InterventionDrugStartTime, "InterventionDrugStartTime");
        ensureInAttributeMetadata(TagFromName.InterventionDrugStopTime, "InterventionDrugStopTime");
        ensureInAttributeMetadata(TagFromName.InterventionSequence, "InterventionSequence");
        ensureInAttributeMetadata(TagFromName.InterventionStatus, "InterventionStatus");
        ensureInAttributeMetadata(TagFromName.IntraOcularPressure, "IntraOcularPressure");
        ensureInAttributeMetadata(TagFromName.InversionRecovery, "InversionRecovery");
        ensureInAttributeMetadata(TagFromName.InversionTime, "InversionTime");
        ensureInAttributeMetadata(TagFromName.InversionTimes, "InversionTimes");
        ensureInAttributeMetadata(TagFromName.IonBeamLimitingDeviceSequence, "IonBeamLimitingDeviceSequence");
        ensureInAttributeMetadata(TagFromName.IonBeamSequence, "IonBeamSequence");
        ensureInAttributeMetadata(TagFromName.IonBlockSequence, "IonBlockSequence");
        ensureInAttributeMetadata(TagFromName.IonControlPointDeliverySequence, "IonControlPointDeliverySequence");
        ensureInAttributeMetadata(TagFromName.IonControlPointSequence, "IonControlPointSequence");
        ensureInAttributeMetadata(TagFromName.IonRangeCompensatorSequence, "IonRangeCompensatorSequence");
        ensureInAttributeMetadata(TagFromName.IonToleranceTableSequence, "IonToleranceTableSequence");
        ensureInAttributeMetadata(TagFromName.IonWedgePositionSequence, "IonWedgePositionSequence");
        ensureInAttributeMetadata(TagFromName.IonWedgeSequence, "IonWedgeSequence");
        ensureInAttributeMetadata(TagFromName.IrradiationEventIdentificationSequence,
                "IrradiationEventIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.IrradiationEventUID, "IrradiationEventUID");
        ensureInAttributeMetadata(TagFromName.IsocenterPosition, "IsocenterPosition");
        ensureInAttributeMetadata(TagFromName.IsocenterReferenceSystemSequence, "IsocenterReferenceSystemSequence");
        ensureInAttributeMetadata(TagFromName.IsocenterToBeamLimitingDeviceDistance,
                "IsocenterToBeamLimitingDeviceDistance");
        ensureInAttributeMetadata(TagFromName.IsocenterToBlockTrayDistance, "IsocenterToBlockTrayDistance");
        ensureInAttributeMetadata(TagFromName.IsocenterToCompensatorDistances, "IsocenterToCompensatorDistances");
        ensureInAttributeMetadata(TagFromName.IsocenterToCompensatorTrayDistance, "IsocenterToCompensatorTrayDistance");
        ensureInAttributeMetadata(TagFromName.IsocenterToLateralSpreadingDeviceDistance,
                "IsocenterToLateralSpreadingDeviceDistance");
        ensureInAttributeMetadata(TagFromName.IsocenterToRangeModulatorDistance, "IsocenterToRangeModulatorDistance");
        ensureInAttributeMetadata(TagFromName.IsocenterToRangeShifterDistance, "IsocenterToRangeShifterDistance");
        ensureInAttributeMetadata(TagFromName.IsocenterToWedgeTrayDistance, "IsocenterToWedgeTrayDistance");
        ensureInAttributeMetadata(TagFromName.IsotopeNumber, "IsotopeNumber");
        ensureInAttributeMetadata(TagFromName.IssueDateOfImagingServiceRequest, "IssueDateOfImagingServiceRequest");
        // ensureInAttributeMetadata(TagFromName.IssuerOfAccessionNumberSequence,
        // "IssuerOfAccessionNumberSequence");
        ensureInAttributeMetadata(TagFromName.IssuerOfAdmissionID, "IssuerOfAdmissionID");
        // ensureInAttributeMetadata(TagFromName.IssuerOfAdmissionIDSequence,
        // "IssuerOfAdmissionIDSequence");
        ensureInAttributeMetadata(TagFromName.IssuerOfPatientID, "IssuerOfPatientID");
        // ensureInAttributeMetadata(TagFromName.IssuerOfPatientIDQualifiersSequence,
        // "IssuerOfPatientIDQualifiersSequence");
        ensureInAttributeMetadata(TagFromName.IssuerOfServiceEpisodeID, "IssuerOfServiceEpisodeID");
        // ensureInAttributeMetadata(TagFromName.IssuerOfServiceEpisodeIDSequence,
        // "IssuerOfServiceEpisodeIDSequence");
        ensureInAttributeMetadata(TagFromName.IssueTimeOfImagingServiceRequest, "IssueTimeOfImagingServiceRequest");
        ensureInAttributeMetadata(TagFromName.Item, "Item");
        ensureInAttributeMetadata(TagFromName.ItemDelimitationItem, "ItemDelimitationItem");
        ensureInAttributeMetadata(TagFromName.ItemNumber, "ItemNumber");
        ensureInAttributeMetadata(TagFromName.IVUSAcquisition, "IVUSAcquisition");
        ensureInAttributeMetadata(TagFromName.IVUSGatedRate, "IVUSGatedRate");
        ensureInAttributeMetadata(TagFromName.IVUSPullbackRate, "IVUSPullbackRate");
        ensureInAttributeMetadata(TagFromName.IVUSPullbackStartFrameNumber, "IVUSPullbackStartFrameNumber");
        ensureInAttributeMetadata(TagFromName.IVUSPullbackStopFrameNumber, "IVUSPullbackStopFrameNumber");
        ensureInAttributeMetadata(TagFromName.KSpaceFiltering, "KSpaceFiltering");
        ensureInAttributeMetadata(TagFromName.KVP, "KVP");
        ensureInAttributeMetadata(TagFromName.LabelStyleSelection, "LabelStyleSelection");
        ensureInAttributeMetadata(TagFromName.LabelText, "LabelText");
        ensureInAttributeMetadata(TagFromName.LabelUsingInformationExtractedFromInstances,
                "LabelUsingInformationExtractedFromInstances");
        // ensureInAttributeMetadata(TagFromName.LanguageCodeSequenceTrial,
        // "LanguageCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.LargeBluePaletteColorLookupTableData,
                "LargeBluePaletteColorLookupTableData");
        ensureInAttributeMetadata(TagFromName.LargeBluePaletteColorLookupTableDescriptor,
                "LargeBluePaletteColorLookupTableDescriptor");
        ensureInAttributeMetadata(TagFromName.LargeGreenPaletteColorLookupTableData,
                "LargeGreenPaletteColorLookupTableData");
        ensureInAttributeMetadata(TagFromName.LargeGreenPaletteColorLookupTableDescriptor,
                "LargeGreenPaletteColorLookupTableDescriptor");
        ensureInAttributeMetadata(TagFromName.LargePaletteColorLookupTableUID, "LargePaletteColorLookupTableUID");
        ensureInAttributeMetadata(TagFromName.LargeRedPaletteColorLookupTableData,
                "LargeRedPaletteColorLookupTableData");
        ensureInAttributeMetadata(TagFromName.LargeRedPaletteColorLookupTableDescriptor,
                "LargeRedPaletteColorLookupTableDescriptor");
        ensureInAttributeMetadata(TagFromName.LargestImagePixelValue, "LargestImagePixelValue");
        ensureInAttributeMetadata(TagFromName.LargestMonochromePixelValue, "LargestMonochromePixelValue");
        ensureInAttributeMetadata(TagFromName.LargestPixelValueInPlane, "LargestPixelValueInPlane");
        ensureInAttributeMetadata(TagFromName.LargestPixelValueInSeries, "LargestPixelValueInSeries");
        ensureInAttributeMetadata(TagFromName.LargestValidPixelValue, "LargestValidPixelValue");
        ensureInAttributeMetadata(TagFromName.LastMenstrualDate, "LastMenstrualDate");
        ensureInAttributeMetadata(TagFromName.Laterality, "Laterality");
        ensureInAttributeMetadata(TagFromName.LateralSpreadingDeviceDescription, "LateralSpreadingDeviceDescription");
        ensureInAttributeMetadata(TagFromName.LateralSpreadingDeviceID, "LateralSpreadingDeviceID");
        ensureInAttributeMetadata(TagFromName.LateralSpreadingDeviceNumber, "LateralSpreadingDeviceNumber");
        ensureInAttributeMetadata(TagFromName.LateralSpreadingDeviceSequence, "LateralSpreadingDeviceSequence");
        ensureInAttributeMetadata(TagFromName.LateralSpreadingDeviceSetting, "LateralSpreadingDeviceSetting");
        ensureInAttributeMetadata(TagFromName.LateralSpreadingDeviceSettingsSequence,
                "LateralSpreadingDeviceSettingsSequence");
        ensureInAttributeMetadata(TagFromName.LateralSpreadingDeviceType, "LateralSpreadingDeviceType");
        ensureInAttributeMetadata(TagFromName.LateralSpreadingDeviceWaterEquivalentThickness,
                "LateralSpreadingDeviceWaterEquivalentThickness");
        ensureInAttributeMetadata(TagFromName.LeafJawPositions, "LeafJawPositions");
        ensureInAttributeMetadata(TagFromName.LeafPositionBoundaries, "LeafPositionBoundaries");
        ensureInAttributeMetadata(TagFromName.LeftImageSequence, "LeftImageSequence");
        ensureInAttributeMetadata(TagFromName.LengthToEnd, "LengthToEnd");
        ensureInAttributeMetadata(TagFromName.LensesCodeSequence, "LensesCodeSequence");
        ensureInAttributeMetadata(TagFromName.LesionNumber, "LesionNumber");
        ensureInAttributeMetadata(TagFromName.LightPathFilterPassBand, "LightPathFilterPassBand");
        ensureInAttributeMetadata(TagFromName.LightPathFilterPassThroughWavelength,
                "LightPathFilterPassThroughWavelength");
        ensureInAttributeMetadata(TagFromName.LightPathFilterTypeStackCodeSequence,
                "LightPathFilterTypeStackCodeSequence");
        ensureInAttributeMetadata(TagFromName.ListOfMIMETypes, "ListOfMIMETypes");
        // ensureInAttributeMetadata(TagFromName.LocalNamespaceEntityID,
        // "LocalNamespaceEntityID");
        ensureInAttributeMetadata(TagFromName.Location, "Location");
        ensureInAttributeMetadata(TagFromName.LossyImageCompression, "LossyImageCompression");
        ensureInAttributeMetadata(TagFromName.LossyImageCompressionMethod, "LossyImageCompressionMethod");
        ensureInAttributeMetadata(TagFromName.LossyImageCompressionRatio, "LossyImageCompressionRatio");
        ensureInAttributeMetadata(TagFromName.LowerLevelDirectoryOffset, "LowerLevelDirectoryOffset");
        ensureInAttributeMetadata(TagFromName.LowRRValue, "LowRRValue");
        ensureInAttributeMetadata(TagFromName.LUTData, "LUTData");
        ensureInAttributeMetadata(TagFromName.LUTDescriptor, "LUTDescriptor");
        ensureInAttributeMetadata(TagFromName.LUTExplanation, "LUTExplanation");
        ensureInAttributeMetadata(TagFromName.LUTFunction, "LUTFunction");
        ensureInAttributeMetadata(TagFromName.LUTLabel, "LUTLabel");
        ensureInAttributeMetadata(TagFromName.LUTNumber, "LUTNumber");
        ensureInAttributeMetadata(TagFromName.MAC, "MAC");
        ensureInAttributeMetadata(TagFromName.MACAlgorithm, "MACAlgorithm");
        ensureInAttributeMetadata(TagFromName.MACCalculationTransferSyntaxUID, "MACCalculationTransferSyntaxUID");
        ensureInAttributeMetadata(TagFromName.MACIDNumber, "MACIDNumber");
        ensureInAttributeMetadata(TagFromName.MACParametersSequence, "MACParametersSequence");
        ensureInAttributeMetadata(TagFromName.MagneticFieldStrength, "MagneticFieldStrength");
        ensureInAttributeMetadata(TagFromName.MagnetizationTransfer, "MagnetizationTransfer");
        ensureInAttributeMetadata(TagFromName.MagnificationType, "MagnificationType");
        ensureInAttributeMetadata(TagFromName.MagnifyToNumberOfColumns, "MagnifyToNumberOfColumns");
        ensureInAttributeMetadata(TagFromName.ManipulatedImage, "ManipulatedImage");
        ensureInAttributeMetadata(TagFromName.Manufacturer, "Manufacturer");
        ensureInAttributeMetadata(TagFromName.ManufacturerModelName, "ManufacturerModelName");
        ensureInAttributeMetadata(TagFromName.MappingResource, "MappingResource");
        ensureInAttributeMetadata(TagFromName.MaskFrameNumbers, "MaskFrameNumbers");
        ensureInAttributeMetadata(TagFromName.MaskingImage, "MaskingImage");
        ensureInAttributeMetadata(TagFromName.MaskOperation, "MaskOperation");
        ensureInAttributeMetadata(TagFromName.MaskOperationExplanation, "MaskOperationExplanation");
        ensureInAttributeMetadata(TagFromName.MaskPointer, "MaskPointer");
        ensureInAttributeMetadata(TagFromName.MaskSelectionMode, "MaskSelectionMode");
        ensureInAttributeMetadata(TagFromName.MaskSubPixelShift, "MaskSubPixelShift");
        ensureInAttributeMetadata(TagFromName.MaskSubtractionSequence, "MaskSubtractionSequence");
        // ensureInAttributeMetadata(TagFromName.MaskVisibilityPercentage,
        // "MaskVisibilityPercentage");
        ensureInAttributeMetadata(TagFromName.MaterialID, "MaterialID");
        ensureInAttributeMetadata(TagFromName.MatrixRegistrationSequence, "MatrixRegistrationSequence");
        ensureInAttributeMetadata(TagFromName.MatrixSequence, "MatrixSequence");
        ensureInAttributeMetadata(TagFromName.MaxDensity, "MaxDensity");
        ensureInAttributeMetadata(TagFromName.MaximumAcrossScanDistortion, "MaximumAcrossScanDistortion");
        ensureInAttributeMetadata(TagFromName.MaximumAlongScanDistortion, "MaximumAlongScanDistortion");
        ensureInAttributeMetadata(TagFromName.MaximumCollatedFilms, "MaximumCollatedFilms");
        ensureInAttributeMetadata(TagFromName.MaximumCoordinateValue, "MaximumCoordinateValue");
        ensureInAttributeMetadata(TagFromName.MaximumDepthDistortion, "MaximumDepthDistortion");
        ensureInAttributeMetadata(TagFromName.MaximumFractionalValue, "MaximumFractionalValue");
        ensureInAttributeMetadata(TagFromName.MaximumMemoryAllocation, "MaximumMemoryAllocation");
        ensureInAttributeMetadata(TagFromName.MeasuredDoseDescription, "MeasuredDoseDescription");
        ensureInAttributeMetadata(TagFromName.MeasuredDoseReferenceNumber, "MeasuredDoseReferenceNumber");
        ensureInAttributeMetadata(TagFromName.MeasuredDoseReferenceSequence, "MeasuredDoseReferenceSequence");
        ensureInAttributeMetadata(TagFromName.MeasuredDoseType, "MeasuredDoseType");
        ensureInAttributeMetadata(TagFromName.MeasuredDoseValue, "MeasuredDoseValue");
        ensureInAttributeMetadata(TagFromName.MeasuredValueSequence, "MeasuredValueSequence");
        // ensureInAttributeMetadata(TagFromName.MeasurementAutomationTrial,
        // "MeasurementAutomationTrial");
        // ensureInAttributeMetadata(TagFromName.MeasurementPrecisionDescriptionTrial,
        // "MeasurementPrecisionDescriptionTrial");
        ensureInAttributeMetadata(TagFromName.MeasurementUnitsCodeSequence, "MeasurementUnitsCodeSequence");
        ensureInAttributeMetadata(TagFromName.MeasuringUnitsSequence, "MeasuringUnitsSequence");
        ensureInAttributeMetadata(TagFromName.MechanicalIndex, "MechanicalIndex");
        ensureInAttributeMetadata(TagFromName.MediaDisposition, "MediaDisposition");
        ensureInAttributeMetadata(TagFromName.MediaInstalledSequence, "MediaInstalledSequence");
        ensureInAttributeMetadata(TagFromName.MediaStorageSOPClassUID, "MediaStorageSOPClassUID");
        ensureInAttributeMetadata(TagFromName.MediaStorageSOPInstanceUID, "MediaStorageSOPInstanceUID");
        ensureInAttributeMetadata(TagFromName.MedicalAlerts, "MedicalAlerts");
        ensureInAttributeMetadata(TagFromName.MedicalRecordLocator, "MedicalRecordLocator");
        ensureInAttributeMetadata(TagFromName.MediumType, "MediumType");
        ensureInAttributeMetadata(TagFromName.MemoryAllocation, "MemoryAllocation");
        ensureInAttributeMetadata(TagFromName.MemoryBitDepth, "MemoryBitDepth");
        ensureInAttributeMetadata(TagFromName.MessageID, "MessageID");
        ensureInAttributeMetadata(TagFromName.MessageIDBeingRespondedTo, "MessageIDBeingRespondedTo");
        ensureInAttributeMetadata(TagFromName.MessageSetID, "MessageSetID");
        ensureInAttributeMetadata(TagFromName.MetaboliteCodeSequence, "MetaboliteCodeSequence");
        ensureInAttributeMetadata(TagFromName.MetaboliteMapDescription, "MetaboliteMapDescription");
        ensureInAttributeMetadata(TagFromName.MetersetExposure, "MetersetExposure");
        ensureInAttributeMetadata(TagFromName.MetersetRate, "MetersetRate");
        ensureInAttributeMetadata(TagFromName.MetersetRateDelivered, "MetersetRateDelivered");
        ensureInAttributeMetadata(TagFromName.MetersetRateSet, "MetersetRateSet");
        ensureInAttributeMetadata(TagFromName.MidSlabPosition, "MidSlabPosition");
        ensureInAttributeMetadata(TagFromName.MilitaryRank, "MilitaryRank");
        ensureInAttributeMetadata(TagFromName.MIMETypeOfEncapsulatedDocument, "MIMETypeOfEncapsulatedDocument");
        ensureInAttributeMetadata(TagFromName.MinDensity, "MinDensity");
        ensureInAttributeMetadata(TagFromName.MinimumCoordinateValue, "MinimumCoordinateValue");
        ensureInAttributeMetadata(TagFromName.ModalitiesInStudy, "ModalitiesInStudy");
        ensureInAttributeMetadata(TagFromName.Modality, "Modality");
        ensureInAttributeMetadata(TagFromName.ModalityLUTSequence, "ModalityLUTSequence");
        ensureInAttributeMetadata(TagFromName.ModalityLUTType, "ModalityLUTType");
        ensureInAttributeMetadata(TagFromName.ModifiedAttributesSequence, "ModifiedAttributesSequence");
        ensureInAttributeMetadata(TagFromName.ModifiedImageDate, "ModifiedImageDate");
        ensureInAttributeMetadata(TagFromName.ModifiedImageDescription, "ModifiedImageDescription");
        ensureInAttributeMetadata(TagFromName.ModifiedImageID, "ModifiedImageID");
        ensureInAttributeMetadata(TagFromName.ModifiedImageTime, "ModifiedImageTime");
        ensureInAttributeMetadata(TagFromName.ModifierCodeSequence, "ModifierCodeSequence");
        ensureInAttributeMetadata(TagFromName.ModifyingDeviceID, "ModifyingDeviceID");
        ensureInAttributeMetadata(TagFromName.ModifyingDeviceManufacturer, "ModifyingDeviceManufacturer");
        ensureInAttributeMetadata(TagFromName.ModifyingSystem, "ModifyingSystem");
        ensureInAttributeMetadata(TagFromName.MostRecentTreatmentDate, "MostRecentTreatmentDate");
        ensureInAttributeMetadata(TagFromName.MotionSynchronizationSequence, "MotionSynchronizationSequence");
        ensureInAttributeMetadata(TagFromName.MoveDestination, "MoveDestination");
        ensureInAttributeMetadata(TagFromName.MoveOriginatorApplicationEntityTitle,
                "MoveOriginatorApplicationEntityTitle");
        ensureInAttributeMetadata(TagFromName.MoveOriginatorMessageID, "MoveOriginatorMessageID");
        ensureInAttributeMetadata(TagFromName.MRAcquisitionFrequencyEncodingSteps,
                "MRAcquisitionFrequencyEncodingSteps");
        ensureInAttributeMetadata(TagFromName.MRAcquisitionPhaseEncodingStepsInPlane,
                "MRAcquisitionPhaseEncodingStepsInPlane");
        ensureInAttributeMetadata(TagFromName.MRAcquisitionPhaseEncodingStepsOutOfPlane,
                "MRAcquisitionPhaseEncodingStepsOutOfPlane");
        ensureInAttributeMetadata(TagFromName.MRAcquisitionType, "MRAcquisitionType");
        ensureInAttributeMetadata(TagFromName.MRAveragesSequence, "MRAveragesSequence");
        ensureInAttributeMetadata(TagFromName.MRDiffusionSequence, "MRDiffusionSequence");
        ensureInAttributeMetadata(TagFromName.MRDRDirectoryRecordOffset, "MRDRDirectoryRecordOffset");
        ensureInAttributeMetadata(TagFromName.MREchoSequence, "MREchoSequence");
        ensureInAttributeMetadata(TagFromName.MRFOVGeometrySequence, "MRFOVGeometrySequence");
        ensureInAttributeMetadata(TagFromName.MRImageFrameTypeSequence, "MRImageFrameTypeSequence");
        ensureInAttributeMetadata(TagFromName.MRImagingModifierSequence, "MRImagingModifierSequence");
        ensureInAttributeMetadata(TagFromName.MRMetaboliteMapSequence, "MRMetaboliteMapSequence");
        ensureInAttributeMetadata(TagFromName.MRModifierSequence, "MRModifierSequence");
        ensureInAttributeMetadata(TagFromName.MRReceiveCoilSequence, "MRReceiveCoilSequence");
        ensureInAttributeMetadata(TagFromName.MRSpatialSaturationSequence, "MRSpatialSaturationSequence");
        ensureInAttributeMetadata(TagFromName.MRSpectroscopyAcquisitionType, "MRSpectroscopyAcquisitionType");
        ensureInAttributeMetadata(TagFromName.MRSpectroscopyFOVGeometrySequence, "MRSpectroscopyFOVGeometrySequence");
        ensureInAttributeMetadata(TagFromName.MRSpectroscopyFrameTypeSequence, "MRSpectroscopyFrameTypeSequence");
        ensureInAttributeMetadata(TagFromName.MRTimingAndRelatedParametersSequence,
                "MRTimingAndRelatedParametersSequence");
        ensureInAttributeMetadata(TagFromName.MRTransmitCoilSequence, "MRTransmitCoilSequence");
        ensureInAttributeMetadata(TagFromName.MRVelocityEncodingSequence, "MRVelocityEncodingSequence");
        ensureInAttributeMetadata(TagFromName.MultiCoilConfiguration, "MultiCoilConfiguration");
        ensureInAttributeMetadata(TagFromName.MultiCoilDefinitionSequence, "MultiCoilDefinitionSequence");
        ensureInAttributeMetadata(TagFromName.MultiCoilElementName, "MultiCoilElementName");
        ensureInAttributeMetadata(TagFromName.MultiCoilElementUsed, "MultiCoilElementUsed");
        ensureInAttributeMetadata(TagFromName.MultiplanarExcitation, "MultiplanarExcitation");
        ensureInAttributeMetadata(TagFromName.MultipleCopiesFlag, "MultipleCopiesFlag");
        ensureInAttributeMetadata(TagFromName.MultipleSpinEcho, "MultipleSpinEcho");
        ensureInAttributeMetadata(TagFromName.MultiplexedAudioChannelsDescriptionCodeSequence,
                "MultiplexedAudioChannelsDescriptionCodeSequence");
        ensureInAttributeMetadata(TagFromName.MultiplexGroupLabel, "MultiplexGroupLabel");
        ensureInAttributeMetadata(TagFromName.MultiplexGroupTimeOffset, "MultiplexGroupTimeOffset");
        ensureInAttributeMetadata(TagFromName.MydriaticAgentCodeSequence, "MydriaticAgentCodeSequence");
        ensureInAttributeMetadata(TagFromName.MydriaticAgentConcentration, "MydriaticAgentConcentration");
        ensureInAttributeMetadata(TagFromName.MydriaticAgentConcentrationUnitsSequence,
                "MydriaticAgentConcentrationUnitsSequence");
        ensureInAttributeMetadata(TagFromName.MydriaticAgentSequence, "MydriaticAgentSequence");
        ensureInAttributeMetadata(TagFromName.NamesOfIntendedRecipientsOfResults, "NamesOfIntendedRecipientsOfResults");
        ensureInAttributeMetadata(TagFromName.NavigationDisplaySet, "NavigationDisplaySet");
        ensureInAttributeMetadata(TagFromName.NavigationIndicatorSequence, "NavigationIndicatorSequence");
        ensureInAttributeMetadata(TagFromName.NetworkID, "NetworkID");
        ensureInAttributeMetadata(TagFromName.NextDirectoryRecordOffset, "NextDirectoryRecordOffset");
        ensureInAttributeMetadata(TagFromName.NominalBeamEnergy, "NominalBeamEnergy");
        ensureInAttributeMetadata(TagFromName.NominalBeamEnergyUnit, "NominalBeamEnergyUnit");
        ensureInAttributeMetadata(TagFromName.NominalCardiacTriggerDelayTime, "NominalCardiacTriggerDelayTime");
        ensureInAttributeMetadata(TagFromName.NominalInterval, "NominalInterval");
        ensureInAttributeMetadata(TagFromName.NominalPercentageOfCardiacPhase, "NominalPercentageOfCardiacPhase");
        ensureInAttributeMetadata(TagFromName.NominalPercentageOfRespiratoryPhase,
                "NominalPercentageOfRespiratoryPhase");
        ensureInAttributeMetadata(TagFromName.NominalPriorDose, "NominalPriorDose");
        ensureInAttributeMetadata(TagFromName.NominalRespiratoryTriggerDelayTime, "NominalRespiratoryTriggerDelayTime");
        ensureInAttributeMetadata(TagFromName.NominalScannedPixelSpacing, "NominalScannedPixelSpacing");
        ensureInAttributeMetadata(TagFromName.NominalScreenDefinitionSequence, "NominalScreenDefinitionSequence");
        ensureInAttributeMetadata(TagFromName.NonDICOMOutputCodeSequence, "NonDICOMOutputCodeSequence");
        ensureInAttributeMetadata(TagFromName.NormalizationFactorFormat, "NormalizationFactorFormat");
        ensureInAttributeMetadata(TagFromName.NormalizationPoint, "NormalizationPoint");
        ensureInAttributeMetadata(TagFromName.NormalReverse, "NormalReverse");
        ensureInAttributeMetadata(TagFromName.NotchFilterBandwidth, "NotchFilterBandwidth");
        ensureInAttributeMetadata(TagFromName.NotchFilterFrequency, "NotchFilterFrequency");
        ensureInAttributeMetadata(TagFromName.NTPSourceAddress, "NTPSourceAddress");
        ensureInAttributeMetadata(TagFromName.NuclearMedicineSeriesTypeRetired, "NuclearMedicineSeriesTypeRetired");
        ensureInAttributeMetadata(TagFromName.NumberOfAverages, "NumberOfAverages");
        ensureInAttributeMetadata(TagFromName.NumberOfBeams, "NumberOfBeams");
        ensureInAttributeMetadata(TagFromName.NumberOfBlocks, "NumberOfBlocks");
        ensureInAttributeMetadata(TagFromName.NumberOfBoli, "NumberOfBoli");
        ensureInAttributeMetadata(TagFromName.NumberOfBrachyApplicationSetups, "NumberOfBrachyApplicationSetups");
        ensureInAttributeMetadata(TagFromName.NumberOfChannels, "NumberOfChannels");
        ensureInAttributeMetadata(TagFromName.NumberOfCompensators, "NumberOfCompensators");
        ensureInAttributeMetadata(TagFromName.NumberOfCompletedSuboperations, "NumberOfCompletedSuboperations");
        ensureInAttributeMetadata(TagFromName.NumberOfContourPoints, "NumberOfContourPoints");
        ensureInAttributeMetadata(TagFromName.NumberOfControlPoints, "NumberOfControlPoints");
        ensureInAttributeMetadata(TagFromName.NumberOfCopies, "NumberOfCopies");
        ensureInAttributeMetadata(TagFromName.NumberOfDetectors, "NumberOfDetectors");
        ensureInAttributeMetadata(TagFromName.NumberOfEnergyWindows, "NumberOfEnergyWindows");
        ensureInAttributeMetadata(TagFromName.NumberOfEventTimers, "NumberOfEventTimers");
        ensureInAttributeMetadata(TagFromName.NumberOfFailedSuboperations, "NumberOfFailedSuboperations");
        ensureInAttributeMetadata(TagFromName.NumberOfFilms, "NumberOfFilms");
        ensureInAttributeMetadata(TagFromName.NumberOfFractionPatternDigitsPerDay,
                "NumberOfFractionPatternDigitsPerDay");
        ensureInAttributeMetadata(TagFromName.NumberOfFractionsDelivered, "NumberOfFractionsDelivered");
        ensureInAttributeMetadata(TagFromName.NumberOfFractionsPlanned, "NumberOfFractionsPlanned");
        ensureInAttributeMetadata(TagFromName.NumberOfFrames, "NumberOfFrames");
        ensureInAttributeMetadata(TagFromName.NumberOfFramesInOverlay, "NumberOfFramesInOverlay");
        ensureInAttributeMetadata(TagFromName.NumberOfFramesInPhase, "NumberOfFramesInPhase");
        ensureInAttributeMetadata(TagFromName.NumberOfFramesInRotation, "NumberOfFramesInRotation");
        ensureInAttributeMetadata(TagFromName.NumberOfGraphicPoints, "NumberOfGraphicPoints");
        ensureInAttributeMetadata(TagFromName.NumberOfHorizontalPixels, "NumberOfHorizontalPixels");
        ensureInAttributeMetadata(TagFromName.NumberOfKSpaceTrajectories, "NumberOfKSpaceTrajectories");
        ensureInAttributeMetadata(TagFromName.NumberOfLateralSpreadingDevices, "NumberOfLateralSpreadingDevices");
        ensureInAttributeMetadata(TagFromName.NumberOfLeafJawPairs, "NumberOfLeafJawPairs");
        ensureInAttributeMetadata(TagFromName.NumberOfMatches, "NumberOfMatches");
        ensureInAttributeMetadata(TagFromName.NumberOfPaintings, "NumberOfPaintings");
        ensureInAttributeMetadata(TagFromName.NumberOfPatientRelatedInstances, "NumberOfPatientRelatedInstances");
        ensureInAttributeMetadata(TagFromName.NumberOfPatientRelatedSeries, "NumberOfPatientRelatedSeries");
        ensureInAttributeMetadata(TagFromName.NumberOfPatientRelatedStudies, "NumberOfPatientRelatedStudies");
        ensureInAttributeMetadata(TagFromName.NumberOfPhaseEncodingSteps, "NumberOfPhaseEncodingSteps");
        ensureInAttributeMetadata(TagFromName.NumberOfPhases, "NumberOfPhases");
        ensureInAttributeMetadata(TagFromName.NumberOfPoints, "NumberOfPoints");
        ensureInAttributeMetadata(TagFromName.NumberOfPriorsReferenced, "NumberOfPriorsReferenced");
        ensureInAttributeMetadata(TagFromName.NumberOfPulses, "NumberOfPulses");
        ensureInAttributeMetadata(TagFromName.NumberOfRangeModulators, "NumberOfRangeModulators");
        ensureInAttributeMetadata(TagFromName.NumberOfRangeShifters, "NumberOfRangeShifters");
        ensureInAttributeMetadata(TagFromName.NumberOfReferences, "NumberOfReferences");
        ensureInAttributeMetadata(TagFromName.NumberOfRemainingSuboperations, "NumberOfRemainingSuboperations");
        ensureInAttributeMetadata(TagFromName.NumberOfRotations, "NumberOfRotations");
        ensureInAttributeMetadata(TagFromName.NumberOfRRIntervals, "NumberOfRRIntervals");
        ensureInAttributeMetadata(TagFromName.NumberOfSamples, "NumberOfSamples");
        ensureInAttributeMetadata(TagFromName.NumberOfScanSpotPositions, "NumberOfScanSpotPositions");
        ensureInAttributeMetadata(TagFromName.NumberOfScreens, "NumberOfScreens");
        ensureInAttributeMetadata(TagFromName.NumberOfSeriesRelatedInstances, "NumberOfSeriesRelatedInstances");
        ensureInAttributeMetadata(TagFromName.NumberOfSlices, "NumberOfSlices");
        ensureInAttributeMetadata(TagFromName.NumberOfStages, "NumberOfStages");
        ensureInAttributeMetadata(TagFromName.NumberOfStudyRelatedInstances, "NumberOfStudyRelatedInstances");
        ensureInAttributeMetadata(TagFromName.NumberOfStudyRelatedSeries, "NumberOfStudyRelatedSeries");
        ensureInAttributeMetadata(TagFromName.NumberOfTableBreakPoints, "NumberOfTableBreakPoints");
        ensureInAttributeMetadata(TagFromName.NumberOfTableEntries, "NumberOfTableEntries");
        ensureInAttributeMetadata(TagFromName.NumberOfTables, "NumberOfTables");
        ensureInAttributeMetadata(TagFromName.NumberOfTemporalPositions, "NumberOfTemporalPositions");
        ensureInAttributeMetadata(TagFromName.NumberOfTimeSlices, "NumberOfTimeSlices");
        ensureInAttributeMetadata(TagFromName.NumberOfTimeSlots, "NumberOfTimeSlots");
        ensureInAttributeMetadata(TagFromName.NumberOfTomosynthesisSourceImages, "NumberOfTomosynthesisSourceImages");
        ensureInAttributeMetadata(TagFromName.NumberOfTriggersInPhase, "NumberOfTriggersInPhase");
        ensureInAttributeMetadata(TagFromName.NumberOfVerticalPixels, "NumberOfVerticalPixels");
        ensureInAttributeMetadata(TagFromName.NumberOfViewsInStage, "NumberOfViewsInStage");
        ensureInAttributeMetadata(TagFromName.NumberOfWarningSuboperations, "NumberOfWarningSuboperations");
        ensureInAttributeMetadata(TagFromName.NumberOfWaveformChannels, "NumberOfWaveformChannels");
        ensureInAttributeMetadata(TagFromName.NumberOfWaveformSamples, "NumberOfWaveformSamples");
        ensureInAttributeMetadata(TagFromName.NumberOfWedges, "NumberOfWedges");
        ensureInAttributeMetadata(TagFromName.NumberOfZeroFills, "NumberOfZeroFills");
        ensureInAttributeMetadata(TagFromName.NumericValue, "NumericValue");
        ensureInAttributeMetadata(TagFromName.NumericValueQualifierCodeSequence, "NumericValueQualifierCodeSequence");
        // ensureInAttributeMetadata(TagFromName.ObjectBinaryIdentifierTrial,
        // "ObjectBinaryIdentifierTrial");
        // ensureInAttributeMetadata(TagFromName.ObjectDirectoryBinaryIdentifierTrial,
        // "ObjectDirectoryBinaryIdentifierTrial");
        ensureInAttributeMetadata(TagFromName.ObjectPixelSpacingInCenterOfBeam, "ObjectPixelSpacingInCenterOfBeam");
        ensureInAttributeMetadata(TagFromName.ObjectThicknessSequence, "ObjectThicknessSequence");
        // ensureInAttributeMetadata(TagFromName.ObservationCategoryCodeSequenceTrial,
        // "ObservationCategoryCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.ObservationDateTime, "ObservationDateTime");
        // ensureInAttributeMetadata(TagFromName.ObservationDateTrial,
        // "ObservationDateTrial");
        ensureInAttributeMetadata(TagFromName.ObservationNumber, "ObservationNumber");
        // ensureInAttributeMetadata(TagFromName.ObservationSubjectClassTrial,
        // "ObservationSubjectClassTrial");
        // ensureInAttributeMetadata(TagFromName.ObservationSubjectContextFlagTrial,
        // "ObservationSubjectContextFlagTrial");
        // ensureInAttributeMetadata(TagFromName.ObservationSubjectTypeCodeSequenceTrial,
        // "ObservationSubjectTypeCodeSequenceTrial");
        // ensureInAttributeMetadata(TagFromName.ObservationSubjectUIDTrial,
        // "ObservationSubjectUIDTrial");
        // ensureInAttributeMetadata(TagFromName.ObservationTimeTrial,
        // "ObservationTimeTrial");
        // ensureInAttributeMetadata(TagFromName.ObservationUIDTrial,
        // "ObservationUIDTrial");
        // ensureInAttributeMetadata(TagFromName.ObserverContextFlagTrial,
        // "ObserverContextFlagTrial");
        ensureInAttributeMetadata(TagFromName.ObserverType, "ObserverType");
        ensureInAttributeMetadata(TagFromName.Occupation, "Occupation");
        ensureInAttributeMetadata(TagFromName.OffendingElement, "OffendingElement");
        ensureInAttributeMetadata(TagFromName.OldDataSetSubtype, "OldDataSetSubtype");
        ensureInAttributeMetadata(TagFromName.OldDataSetType, "OldDataSetType");
        ensureInAttributeMetadata(TagFromName.OldLossyImageCompression, "OldLossyImageCompression");
        ensureInAttributeMetadata(TagFromName.OldMagnificationType, "OldMagnificationType");
        ensureInAttributeMetadata(TagFromName.OnAxisBackgroundAnatomicStructureCodeSequenceTrial,
                "OnAxisBackgroundAnatomicStructureCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.OperatingMode, "OperatingMode");
        ensureInAttributeMetadata(TagFromName.OperatingModeSequence, "OperatingModeSequence");
        ensureInAttributeMetadata(TagFromName.OperatingModeType, "OperatingModeType");
        ensureInAttributeMetadata(TagFromName.OperatorIdentificationSequence, "OperatorIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.OperatorName, "OperatorName");
        ensureInAttributeMetadata(TagFromName.OphthalmicFrameLocationSequence, "OphthalmicFrameLocationSequence");
        ensureInAttributeMetadata(TagFromName.OphthalmicImageOrientation, "OphthalmicImageOrientation");
        ensureInAttributeMetadata(TagFromName.OrderCallbackPhoneNumber, "OrderCallbackPhoneNumber");
        ensureInAttributeMetadata(TagFromName.OrderEnteredBy, "OrderEnteredBy");
        ensureInAttributeMetadata(TagFromName.OrderEntererLocation, "OrderEntererLocation");
        // ensureInAttributeMetadata(TagFromName.OrderFillerIdentifierSequence,
        // "OrderFillerIdentifierSequence");
        // ensureInAttributeMetadata(TagFromName.OrderPlacerIdentifierSequence,
        // "OrderPlacerIdentifierSequence");
        ensureInAttributeMetadata(TagFromName.OrganAtRiskFullVolumeDose, "OrganAtRiskFullVolumeDose");
        ensureInAttributeMetadata(TagFromName.OrganAtRiskLimitDose, "OrganAtRiskLimitDose");
        ensureInAttributeMetadata(TagFromName.OrganAtRiskMaximumDose, "OrganAtRiskMaximumDose");
        ensureInAttributeMetadata(TagFromName.OrganAtRiskOverdoseVolumeFraction, "OrganAtRiskOverdoseVolumeFraction");
        ensureInAttributeMetadata(TagFromName.OrganDose, "OrganDose");
        ensureInAttributeMetadata(TagFromName.OrganExposed, "OrganExposed");
        ensureInAttributeMetadata(TagFromName.OriginalAttributesSequence, "OriginalAttributesSequence");
        ensureInAttributeMetadata(TagFromName.OriginalImageIdentification, "OriginalImageIdentification");
        ensureInAttributeMetadata(TagFromName.OriginalImageIdentificationNomenclature,
                "OriginalImageIdentificationNomenclature");
        ensureInAttributeMetadata(TagFromName.OriginalImageSequence, "OriginalImageSequence");
        ensureInAttributeMetadata(TagFromName.OriginalSpecializedSOPClassUID, "OriginalSpecializedSOPClassUID");
        ensureInAttributeMetadata(TagFromName.Originator, "Originator");
        ensureInAttributeMetadata(TagFromName.OtherMagnificationTypesAvailable, "OtherMagnificationTypesAvailable");
        ensureInAttributeMetadata(TagFromName.OtherMediaAvailableSequence, "OtherMediaAvailableSequence");
        ensureInAttributeMetadata(TagFromName.OtherPatientID, "OtherPatientID");
        ensureInAttributeMetadata(TagFromName.OtherPatientIDSequence, "OtherPatientIDSequence");
        ensureInAttributeMetadata(TagFromName.OtherPatientName, "OtherPatientName");
        ensureInAttributeMetadata(TagFromName.OtherSmoothingTypesAvailable, "OtherSmoothingTypesAvailable");
        ensureInAttributeMetadata(TagFromName.OtherStudyNumbers, "OtherStudyNumbers");
        ensureInAttributeMetadata(TagFromName.OutputInformationSequence, "OutputInformationSequence");
        ensureInAttributeMetadata(TagFromName.OutputPower, "OutputPower");
        ensureInAttributeMetadata(TagFromName.OverlayActivationLayer, "OverlayActivationLayer");
        ensureInAttributeMetadata(TagFromName.OverlayBackgroundDensity, "OverlayBackgroundDensity");
        ensureInAttributeMetadata(TagFromName.OverlayBitPosition, "OverlayBitPosition");
        ensureInAttributeMetadata(TagFromName.OverlayBitsAllocated, "OverlayBitsAllocated");
        ensureInAttributeMetadata(TagFromName.OverlayBitsForCodeWord, "OverlayBitsForCodeWord");
        ensureInAttributeMetadata(TagFromName.OverlayBitsGrouped, "OverlayBitsGrouped");
        ensureInAttributeMetadata(TagFromName.OverlayBlue, "OverlayBlue");
        ensureInAttributeMetadata(TagFromName.OverlayCodeLabel, "OverlayCodeLabel");
        ensureInAttributeMetadata(TagFromName.OverlayCodeTableLocation, "OverlayCodeTableLocation");
        ensureInAttributeMetadata(TagFromName.OverlayColumns, "OverlayColumns");
        ensureInAttributeMetadata(TagFromName.OverlayComments, "OverlayComments");
        ensureInAttributeMetadata(TagFromName.OverlayCompressionCode, "OverlayCompressionCode");
        ensureInAttributeMetadata(TagFromName.OverlayCompressionDescription, "OverlayCompressionDescription");
        ensureInAttributeMetadata(TagFromName.OverlayCompressionLabel, "OverlayCompressionLabel");
        ensureInAttributeMetadata(TagFromName.OverlayCompressionOriginator, "OverlayCompressionOriginator");
        ensureInAttributeMetadata(TagFromName.OverlayCompressionStepPointers, "OverlayCompressionStepPointers");
        ensureInAttributeMetadata(TagFromName.OverlayData, "OverlayData");
        ensureInAttributeMetadata(TagFromName.OverlayDate, "OverlayDate");
        ensureInAttributeMetadata(TagFromName.OverlayDescription, "OverlayDescription");
        ensureInAttributeMetadata(TagFromName.OverlayDescriptorBlue, "OverlayDescriptorBlue");
        ensureInAttributeMetadata(TagFromName.OverlayDescriptorGray, "OverlayDescriptorGray");
        ensureInAttributeMetadata(TagFromName.OverlayDescriptorGreen, "OverlayDescriptorGreen");
        ensureInAttributeMetadata(TagFromName.OverlayDescriptorRed, "OverlayDescriptorRed");
        ensureInAttributeMetadata(TagFromName.OverlayForegroundDensity, "OverlayForegroundDensity");
        ensureInAttributeMetadata(TagFromName.OverlayFormat, "OverlayFormat");
        ensureInAttributeMetadata(TagFromName.OverlayGray, "OverlayGray");
        ensureInAttributeMetadata(TagFromName.OverlayGreen, "OverlayGreen");
        ensureInAttributeMetadata(TagFromName.OverlayLabel, "OverlayLabel");
        ensureInAttributeMetadata(TagFromName.OverlayLocation, "OverlayLocation");
        ensureInAttributeMetadata(TagFromName.OverlayMagnificationType, "OverlayMagnificationType");
        ensureInAttributeMetadata(TagFromName.OverlayMode, "OverlayMode");
        ensureInAttributeMetadata(TagFromName.OverlayNumber, "OverlayNumber");
        ensureInAttributeMetadata(TagFromName.OverlayNumberOfTables, "OverlayNumberOfTables");
        ensureInAttributeMetadata(TagFromName.OverlayOrigin, "OverlayOrigin");
        ensureInAttributeMetadata(TagFromName.OverlayOrImageMagnification, "OverlayOrImageMagnification");
        ensureInAttributeMetadata(TagFromName.OverlayPixelDataSequence, "OverlayPixelDataSequence");
        ensureInAttributeMetadata(TagFromName.OverlayPlanes, "OverlayPlanes");
        ensureInAttributeMetadata(TagFromName.OverlayRed, "OverlayRed");
        ensureInAttributeMetadata(TagFromName.OverlayRepeatInterval, "OverlayRepeatInterval");
        ensureInAttributeMetadata(TagFromName.OverlayRows, "OverlayRows");
        ensureInAttributeMetadata(TagFromName.Overlays, "Overlays");
        ensureInAttributeMetadata(TagFromName.OverlaySmoothingType, "OverlaySmoothingType");
        ensureInAttributeMetadata(TagFromName.OverlaySubtype, "OverlaySubtype");
        ensureInAttributeMetadata(TagFromName.OverlayTime, "OverlayTime");
        ensureInAttributeMetadata(TagFromName.OverlayType, "OverlayType");
        ensureInAttributeMetadata(TagFromName.OverrideParameterPointer, "OverrideParameterPointer");
        ensureInAttributeMetadata(TagFromName.OverrideReason, "OverrideReason");
        ensureInAttributeMetadata(TagFromName.OverrideSequence, "OverrideSequence");
        ensureInAttributeMetadata(TagFromName.OversamplingPhase, "OversamplingPhase");
        ensureInAttributeMetadata(TagFromName.OwnerID, "OwnerID");
        ensureInAttributeMetadata(TagFromName.PageNumberVector, "PageNumberVector");
        ensureInAttributeMetadata(TagFromName.PagePositionID, "PagePositionID");
        ensureInAttributeMetadata(TagFromName.PaletteColorLookupTableUID, "PaletteColorLookupTableUID");
        ensureInAttributeMetadata(TagFromName.ParallelAcquisition, "ParallelAcquisition");
        ensureInAttributeMetadata(TagFromName.ParallelAcquisitionTechnique, "ParallelAcquisitionTechnique");
        ensureInAttributeMetadata(TagFromName.ParallelReductionFactorInPlane, "ParallelReductionFactorInPlane");
        ensureInAttributeMetadata(TagFromName.ParallelReductionFactorInPlaneRetired,
                "ParallelReductionFactorInPlaneRetired");
        ensureInAttributeMetadata(TagFromName.ParallelReductionFactorOutOfPlane, "ParallelReductionFactorOutOfPlane");
        ensureInAttributeMetadata(TagFromName.ParallelReductionFactorSecondInPlane,
                "ParallelReductionFactorSecondInPlane");
        ensureInAttributeMetadata(TagFromName.ParameterItemIndex, "ParameterItemIndex");
        ensureInAttributeMetadata(TagFromName.ParameterPointer, "ParameterPointer");
        ensureInAttributeMetadata(TagFromName.ParameterSequencePointer, "ParameterSequencePointer");
        ensureInAttributeMetadata(TagFromName.PartialDataDisplayHandling, "PartialDataDisplayHandling");
        ensureInAttributeMetadata(TagFromName.PartialFourier, "PartialFourier");
        ensureInAttributeMetadata(TagFromName.PartialFourierDirection, "PartialFourierDirection");
        ensureInAttributeMetadata(TagFromName.PartialView, "PartialView");
        ensureInAttributeMetadata(TagFromName.PartialViewCodeSequence, "PartialViewCodeSequence");
        ensureInAttributeMetadata(TagFromName.PartialViewDescription, "PartialViewDescription");
        ensureInAttributeMetadata(TagFromName.ParticipantSequence, "ParticipantSequence");
        ensureInAttributeMetadata(TagFromName.ParticipationDateTime, "ParticipationDateTime");
        ensureInAttributeMetadata(TagFromName.ParticipationType, "ParticipationType");
        ensureInAttributeMetadata(TagFromName.PatientAdditionalPosition, "PatientAdditionalPosition");
        ensureInAttributeMetadata(TagFromName.PatientAddress, "PatientAddress");
        ensureInAttributeMetadata(TagFromName.PatientAge, "PatientAge");
        ensureInAttributeMetadata(TagFromName.PatientBirthDate, "PatientBirthDate");
        ensureInAttributeMetadata(TagFromName.PatientBirthName, "PatientBirthName");
        ensureInAttributeMetadata(TagFromName.PatientBirthTime, "PatientBirthTime");
        ensureInAttributeMetadata(TagFromName.PatientBreedCodeSequence, "PatientBreedCodeSequence");
        ensureInAttributeMetadata(TagFromName.PatientBreedDescription, "PatientBreedDescription");
        ensureInAttributeMetadata(TagFromName.PatientClinicalTrialParticipationSequence,
                "PatientClinicalTrialParticipationSequence");
        ensureInAttributeMetadata(TagFromName.PatientComments, "PatientComments");
        ensureInAttributeMetadata(TagFromName.PatientEyeMovementCommanded, "PatientEyeMovementCommanded");
        ensureInAttributeMetadata(TagFromName.PatientEyeMovementCommandedCodeSequence,
                "PatientEyeMovementCommandedCodeSequence");
        ensureInAttributeMetadata(TagFromName.PatientGantryRelationshipCodeSequence,
                "PatientGantryRelationshipCodeSequence");
        ensureInAttributeMetadata(TagFromName.PatientID, "PatientID");
        ensureInAttributeMetadata(TagFromName.PatientIdentityRemoved, "PatientIdentityRemoved");
        ensureInAttributeMetadata(TagFromName.PatientInstitutionResidence, "PatientInstitutionResidence");
        ensureInAttributeMetadata(TagFromName.PatientInsurancePlanCodeSequence, "PatientInsurancePlanCodeSequence");
        ensureInAttributeMetadata(TagFromName.PatientMotherBirthName, "PatientMotherBirthName");
        ensureInAttributeMetadata(TagFromName.PatientName, "PatientName");
        ensureInAttributeMetadata(TagFromName.PatientOrientation, "PatientOrientation");
        ensureInAttributeMetadata(TagFromName.PatientOrientationCodeSequence, "PatientOrientationCodeSequence");
        ensureInAttributeMetadata(TagFromName.PatientOrientationInFrameSequence, "PatientOrientationInFrameSequence");
        ensureInAttributeMetadata(TagFromName.PatientOrientationModifierCodeSequence,
                "PatientOrientationModifierCodeSequence");
        ensureInAttributeMetadata(TagFromName.PatientPosition, "PatientPosition");
        ensureInAttributeMetadata(TagFromName.PatientPrimaryLanguageCodeSequence, "PatientPrimaryLanguageCodeSequence");
        ensureInAttributeMetadata(TagFromName.PatientPrimaryLanguageModifierCodeSequence,
                "PatientPrimaryLanguageModifierCodeSequence");
        ensureInAttributeMetadata(TagFromName.PatientReligiousPreference, "PatientReligiousPreference");
        ensureInAttributeMetadata(TagFromName.PatientSetupLabel, "PatientSetupLabel");
        ensureInAttributeMetadata(TagFromName.PatientSetupNumber, "PatientSetupNumber");
        ensureInAttributeMetadata(TagFromName.PatientSetupSequence, "PatientSetupSequence");
        ensureInAttributeMetadata(TagFromName.PatientSex, "PatientSex");
        ensureInAttributeMetadata(TagFromName.PatientSexNeutered, "PatientSexNeutered");
        ensureInAttributeMetadata(TagFromName.PatientSize, "PatientSize");
        ensureInAttributeMetadata(TagFromName.PatientSpeciesCodeSequence, "PatientSpeciesCodeSequence");
        ensureInAttributeMetadata(TagFromName.PatientSpeciesDescription, "PatientSpeciesDescription");
        ensureInAttributeMetadata(TagFromName.PatientState, "PatientState");
        ensureInAttributeMetadata(TagFromName.PatientSupportAccessoryCode, "PatientSupportAccessoryCode");
        ensureInAttributeMetadata(TagFromName.PatientSupportAngle, "PatientSupportAngle");
        ensureInAttributeMetadata(TagFromName.PatientSupportAngleTolerance, "PatientSupportAngleTolerance");
        ensureInAttributeMetadata(TagFromName.PatientSupportID, "PatientSupportID");
        ensureInAttributeMetadata(TagFromName.PatientSupportRotationDirection, "PatientSupportRotationDirection");
        ensureInAttributeMetadata(TagFromName.PatientSupportType, "PatientSupportType");
        ensureInAttributeMetadata(TagFromName.PatientTelephoneNumber, "PatientTelephoneNumber");
        ensureInAttributeMetadata(TagFromName.PatientTransportArrangements, "PatientTransportArrangements");
        ensureInAttributeMetadata(TagFromName.PatientWeight, "PatientWeight");
        ensureInAttributeMetadata(TagFromName.PauseBetweenFrames, "PauseBetweenFrames");
        ensureInAttributeMetadata(TagFromName.PercentPhaseFieldOfView, "PercentPhaseFieldOfView");
        ensureInAttributeMetadata(TagFromName.PercentSampling, "PercentSampling");
        ensureInAttributeMetadata(TagFromName.PerformedLocation, "PerformedLocation");
        ensureInAttributeMetadata(TagFromName.PerformedProcedureCodeSequence, "PerformedProcedureCodeSequence");
        ensureInAttributeMetadata(TagFromName.PerformedProcedureStepDescription, "PerformedProcedureStepDescription");
        ensureInAttributeMetadata(TagFromName.PerformedProcedureStepDiscontinuationReasonCodeSequence,
                "PerformedProcedureStepDiscontinuationReasonCodeSequence");
        ensureInAttributeMetadata(TagFromName.PerformedProcedureStepEndDate, "PerformedProcedureStepEndDate");
        ensureInAttributeMetadata(TagFromName.PerformedProcedureStepEndTime, "PerformedProcedureStepEndTime");
        ensureInAttributeMetadata(TagFromName.PerformedProcedureStepID, "PerformedProcedureStepID");
        ensureInAttributeMetadata(TagFromName.PerformedProcedureStepStartDate, "PerformedProcedureStepStartDate");
        ensureInAttributeMetadata(TagFromName.PerformedProcedureStepStartTime, "PerformedProcedureStepStartTime");
        ensureInAttributeMetadata(TagFromName.PerformedProcedureStepStatus, "PerformedProcedureStepStatus");
        ensureInAttributeMetadata(TagFromName.PerformedProcedureTypeDescription, "PerformedProcedureTypeDescription");
        ensureInAttributeMetadata(TagFromName.PerformedProcessingApplicationsCodeSequence,
                "PerformedProcessingApplicationsCodeSequence");
        ensureInAttributeMetadata(TagFromName.PerformedProtocolCodeSequence, "PerformedProtocolCodeSequence");
        ensureInAttributeMetadata(TagFromName.PerformedSeriesSequence, "PerformedSeriesSequence");
        ensureInAttributeMetadata(TagFromName.PerformedStationAETitle, "PerformedStationAETitle");
        ensureInAttributeMetadata(TagFromName.PerformedStationClassCodeSequence, "PerformedStationClassCodeSequence");
        ensureInAttributeMetadata(TagFromName.PerformedStationGeographicLocationCodeSequence,
                "PerformedStationGeographicLocationCodeSequence");
        ensureInAttributeMetadata(TagFromName.PerformedStationName, "PerformedStationName");
        ensureInAttributeMetadata(TagFromName.PerformedStationNameCodeSequence, "PerformedStationNameCodeSequence");
        ensureInAttributeMetadata(TagFromName.PerformedWorkitemCodeSequence, "PerformedWorkitemCodeSequence");
        ensureInAttributeMetadata(TagFromName.PerformingPhysicianIdentificationSequence,
                "PerformingPhysicianIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.PerformingPhysicianName, "PerformingPhysicianName");
        ensureInAttributeMetadata(TagFromName.PerFrameFunctionalGroupsSequence, "PerFrameFunctionalGroupsSequence");
        ensureInAttributeMetadata(TagFromName.PerimeterTable, "PerimeterTable");
        ensureInAttributeMetadata(TagFromName.PerimeterValue, "PerimeterValue");
        ensureInAttributeMetadata(TagFromName.PerProjectionAcquisitionSequence, "PerProjectionAcquisitionSequence");
        ensureInAttributeMetadata(TagFromName.PersonAddress, "PersonAddress");
        ensureInAttributeMetadata(TagFromName.PersonIdentificationCodeSequence, "PersonIdentificationCodeSequence");
        ensureInAttributeMetadata(TagFromName.PersonName, "PersonName");
        ensureInAttributeMetadata(TagFromName.PersonTelephoneNumbers, "PersonTelephoneNumbers");
        ensureInAttributeMetadata(TagFromName.PertinentDocumentsSequence, "PertinentDocumentsSequence");
        ensureInAttributeMetadata(TagFromName.PertinentOtherEvidenceSequence, "PertinentOtherEvidenceSequence");
        ensureInAttributeMetadata(TagFromName.PhaseContrast, "PhaseContrast");
        ensureInAttributeMetadata(TagFromName.PhaseDelay, "PhaseDelay");
        ensureInAttributeMetadata(TagFromName.PhaseDescription, "PhaseDescription");
        ensureInAttributeMetadata(TagFromName.PhaseInformationSequence, "PhaseInformationSequence");
        ensureInAttributeMetadata(TagFromName.PhaseNumber, "PhaseNumber");
        ensureInAttributeMetadata(TagFromName.PhaseVector, "PhaseVector");
        ensureInAttributeMetadata(TagFromName.PhosphorType, "PhosphorType");
        ensureInAttributeMetadata(TagFromName.PhotometricInterpretation, "PhotometricInterpretation");
        ensureInAttributeMetadata(TagFromName.PhototimerSetting, "PhototimerSetting");
        ensureInAttributeMetadata(TagFromName.PhysicalDeltaX, "PhysicalDeltaX");
        ensureInAttributeMetadata(TagFromName.PhysicalDeltaY, "PhysicalDeltaY");
        ensureInAttributeMetadata(TagFromName.PhysicalDetectorSize, "PhysicalDetectorSize");
        ensureInAttributeMetadata(TagFromName.PhysicalUnitsXDirection, "PhysicalUnitsXDirection");
        ensureInAttributeMetadata(TagFromName.PhysicalUnitsYDirection, "PhysicalUnitsYDirection");
        ensureInAttributeMetadata(TagFromName.PhysicianApprovingInterpretation, "PhysicianApprovingInterpretation");
        ensureInAttributeMetadata(TagFromName.PhysicianOfRecord, "PhysicianOfRecord");
        ensureInAttributeMetadata(TagFromName.PhysicianOfRecordIdentificationSequence,
                "PhysicianOfRecordIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.PhysicianReadingStudy, "PhysicianReadingStudy");
        ensureInAttributeMetadata(TagFromName.PhysicianReadingStudyIdentificationSequence,
                "PhysicianReadingStudyIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.PixelAspectRatio, "PixelAspectRatio");
        ensureInAttributeMetadata(TagFromName.PixelBandwidth, "PixelBandwidth");
        ensureInAttributeMetadata(TagFromName.PixelComponentDataType, "PixelComponentDataType");
        ensureInAttributeMetadata(TagFromName.PixelComponentMask, "PixelComponentMask");
        ensureInAttributeMetadata(TagFromName.PixelComponentOrganization, "PixelComponentOrganization");
        ensureInAttributeMetadata(TagFromName.PixelComponentPhysicalUnits, "PixelComponentPhysicalUnits");
        ensureInAttributeMetadata(TagFromName.PixelComponentRangeStart, "PixelComponentRangeStart");
        ensureInAttributeMetadata(TagFromName.PixelComponentRangeStop, "PixelComponentRangeStop");
        // ensureInAttributeMetadata(TagFromName.PixelCoordinatesSetTrial,
        // "PixelCoordinatesSetTrial");
        ensureInAttributeMetadata(TagFromName.PixelData, "PixelData");
        ensureInAttributeMetadata(TagFromName.PixelDataProviderURL, "PixelDataProviderURL");
        ensureInAttributeMetadata(TagFromName.PixelIntensityRelationship, "PixelIntensityRelationship");
        ensureInAttributeMetadata(TagFromName.PixelIntensityRelationshipLUTSequence,
                "PixelIntensityRelationshipLUTSequence");
        ensureInAttributeMetadata(TagFromName.PixelIntensityRelationshipSign, "PixelIntensityRelationshipSign");
        ensureInAttributeMetadata(TagFromName.PixelMeasuresSequence, "PixelMeasuresSequence");
        ensureInAttributeMetadata(TagFromName.PixelPaddingRangeLimit, "PixelPaddingRangeLimit");
        ensureInAttributeMetadata(TagFromName.PixelPaddingValue, "PixelPaddingValue");
        ensureInAttributeMetadata(TagFromName.PixelPresentation, "PixelPresentation");
        ensureInAttributeMetadata(TagFromName.PixelRepresentation, "PixelRepresentation");
        ensureInAttributeMetadata(TagFromName.PixelSpacing, "PixelSpacing");
        ensureInAttributeMetadata(TagFromName.PixelSpacingCalibrationDescription, "PixelSpacingCalibrationDescription");
        ensureInAttributeMetadata(TagFromName.PixelSpacingCalibrationType, "PixelSpacingCalibrationType");
        ensureInAttributeMetadata(TagFromName.PixelSpacingSequence, "PixelSpacingSequence");
        ensureInAttributeMetadata(TagFromName.PixelValueMappingCodeSequence, "PixelValueMappingCodeSequence");
        ensureInAttributeMetadata(TagFromName.PixelValueTransformationSequence, "PixelValueTransformationSequence");
        ensureInAttributeMetadata(TagFromName.PlacerOrderNumberOfImagingServiceRequest,
                "PlacerOrderNumberOfImagingServiceRequest");
        ensureInAttributeMetadata(TagFromName.PlacerOrderNumberOfImagingServiceRequestRetired,
                "PlacerOrderNumberOfImagingServiceRequestRetired");
        ensureInAttributeMetadata(TagFromName.PlacerOrderNumberOfProcedure, "PlacerOrderNumberOfProcedure");
        ensureInAttributeMetadata(TagFromName.PlanarConfiguration, "PlanarConfiguration");
        ensureInAttributeMetadata(TagFromName.PlaneIdentification, "PlaneIdentification");
        ensureInAttributeMetadata(TagFromName.PlaneOrientationSequence, "PlaneOrientationSequence");
        ensureInAttributeMetadata(TagFromName.PlaneOrigin, "PlaneOrigin");
        ensureInAttributeMetadata(TagFromName.PlanePositionSequence, "PlanePositionSequence");
        ensureInAttributeMetadata(TagFromName.Planes, "Planes");
        ensureInAttributeMetadata(TagFromName.PlanIntent, "PlanIntent");
        ensureInAttributeMetadata(TagFromName.PlannedVerificationImageSequence, "PlannedVerificationImageSequence");
        ensureInAttributeMetadata(TagFromName.PlateID, "PlateID");
        ensureInAttributeMetadata(TagFromName.PlateType, "PlateType");
        ensureInAttributeMetadata(TagFromName.Polarity, "Polarity");
        ensureInAttributeMetadata(TagFromName.PositionerIsocenterDetectorRotationAngle,
                "PositionerIsocenterDetectorRotationAngle");
        ensureInAttributeMetadata(TagFromName.PositionerIsocenterPrimaryAngle, "PositionerIsocenterPrimaryAngle");
        ensureInAttributeMetadata(TagFromName.PositionerIsocenterSecondaryAngle, "PositionerIsocenterSecondaryAngle");
        ensureInAttributeMetadata(TagFromName.PositionerMotion, "PositionerMotion");
        ensureInAttributeMetadata(TagFromName.PositionerPositionSequence, "PositionerPositionSequence");
        ensureInAttributeMetadata(TagFromName.PositionerPrimaryAngle, "PositionerPrimaryAngle");
        ensureInAttributeMetadata(TagFromName.PositionerPrimaryAngleIncrement, "PositionerPrimaryAngleIncrement");
        ensureInAttributeMetadata(TagFromName.PositionerSecondaryAngle, "PositionerSecondaryAngle");
        ensureInAttributeMetadata(TagFromName.PositionerSecondaryAngleIncrement, "PositionerSecondaryAngleIncrement");
        ensureInAttributeMetadata(TagFromName.PositionerType, "PositionerType");
        ensureInAttributeMetadata(TagFromName.PositionOfIsocenterProjection, "PositionOfIsocenterProjection");
        ensureInAttributeMetadata(TagFromName.PositionReferenceIndicator, "PositionReferenceIndicator");
        ensureInAttributeMetadata(TagFromName.PostDeformationMatrixRegistrationSequence,
                "PostDeformationMatrixRegistrationSequence");
        ensureInAttributeMetadata(TagFromName.PostprocessingFunction, "PostprocessingFunction");
        ensureInAttributeMetadata(TagFromName.PredecessorDocumentsSequence, "PredecessorDocumentsSequence");
        ensureInAttributeMetadata(TagFromName.PreDeformationMatrixRegistrationSequence,
                "PreDeformationMatrixRegistrationSequence");
        ensureInAttributeMetadata(TagFromName.PredictorColumns, "PredictorColumns");
        ensureInAttributeMetadata(TagFromName.PredictorConstants, "PredictorConstants");
        ensureInAttributeMetadata(TagFromName.PredictorRows, "PredictorRows");
        ensureInAttributeMetadata(TagFromName.PreferredPlaybackSequencing, "PreferredPlaybackSequencing");
        ensureInAttributeMetadata(TagFromName.PregnancyStatus, "PregnancyStatus");
        ensureInAttributeMetadata(TagFromName.PreMedication, "PreMedication");
        ensureInAttributeMetadata(TagFromName.PrescriptionDescription, "PrescriptionDescription");
        ensureInAttributeMetadata(TagFromName.PresentationCreationDate, "PresentationCreationDate");
        ensureInAttributeMetadata(TagFromName.PresentationCreationTime, "PresentationCreationTime");
        ensureInAttributeMetadata(TagFromName.PresentationGroupNumber, "PresentationGroupNumber");
        ensureInAttributeMetadata(TagFromName.PresentationIntentType, "PresentationIntentType");
        ensureInAttributeMetadata(TagFromName.PresentationLUTContentSequence, "PresentationLUTContentSequence");
        ensureInAttributeMetadata(TagFromName.PresentationLUTFlag, "PresentationLUTFlag");
        ensureInAttributeMetadata(TagFromName.PresentationLUTSequence, "PresentationLUTSequence");
        ensureInAttributeMetadata(TagFromName.PresentationLUTShape, "PresentationLUTShape");
        ensureInAttributeMetadata(TagFromName.PresentationPixelAspectRatio, "PresentationPixelAspectRatio");
        ensureInAttributeMetadata(TagFromName.PresentationPixelMagnificationRatio,
                "PresentationPixelMagnificationRatio");
        ensureInAttributeMetadata(TagFromName.PresentationPixelSpacing, "PresentationPixelSpacing");
        ensureInAttributeMetadata(TagFromName.PresentationSizeMode, "PresentationSizeMode");
        ensureInAttributeMetadata(TagFromName.PreserveCompositeInstancesAfterMediaCreation,
                "PreserveCompositeInstancesAfterMediaCreation");
        ensureInAttributeMetadata(TagFromName.PrimaryAnatomicStructureModifierSequence,
                "PrimaryAnatomicStructureModifierSequence");
        ensureInAttributeMetadata(TagFromName.PrimaryAnatomicStructureSequence, "PrimaryAnatomicStructureSequence");
        ensureInAttributeMetadata(TagFromName.PrimaryDosimeterUnit, "PrimaryDosimeterUnit");
        ensureInAttributeMetadata(TagFromName.PrimaryPositionerIncrement, "PrimaryPositionerIncrement");
        ensureInAttributeMetadata(TagFromName.PrimaryPositionerScanArc, "PrimaryPositionerScanArc");
        ensureInAttributeMetadata(TagFromName.PrimaryPositionerScanStartAngle, "PrimaryPositionerScanStartAngle");
        ensureInAttributeMetadata(TagFromName.PrimaryPromptsCountsAccumulated, "PrimaryPromptsCountsAccumulated");
        ensureInAttributeMetadata(TagFromName.Print, "Print");
        ensureInAttributeMetadata(TagFromName.PrinterCharacteristicsSequence, "PrinterCharacteristicsSequence");
        ensureInAttributeMetadata(TagFromName.PrinterConfigurationSequence, "PrinterConfigurationSequence");
        ensureInAttributeMetadata(TagFromName.PrinterName, "PrinterName");
        ensureInAttributeMetadata(TagFromName.PrinterPixelSpacing, "PrinterPixelSpacing");
        ensureInAttributeMetadata(TagFromName.PrinterResolutionID, "PrinterResolutionID");
        ensureInAttributeMetadata(TagFromName.PrinterStatus, "PrinterStatus");
        ensureInAttributeMetadata(TagFromName.PrinterStatusInfo, "PrinterStatusInfo");
        ensureInAttributeMetadata(TagFromName.PrintingBitDepth, "PrintingBitDepth");
        ensureInAttributeMetadata(TagFromName.PrintJobDescriptionSequence, "PrintJobDescriptionSequence");
        ensureInAttributeMetadata(TagFromName.PrintJobID, "PrintJobID");
        ensureInAttributeMetadata(TagFromName.PrintManagementCapabilitiesSequence,
                "PrintManagementCapabilitiesSequence");
        ensureInAttributeMetadata(TagFromName.PrintPriority, "PrintPriority");
        ensureInAttributeMetadata(TagFromName.PrintQueueID, "PrintQueueID");
        ensureInAttributeMetadata(TagFromName.Priority, "Priority");
        ensureInAttributeMetadata(TagFromName.PrivateInformation, "PrivateInformation");
        ensureInAttributeMetadata(TagFromName.PrivateInformationCreatorUID, "PrivateInformationCreatorUID");
        ensureInAttributeMetadata(TagFromName.PrivateRecordUID, "PrivateRecordUID");
        ensureInAttributeMetadata(TagFromName.ProcedureCodeSequence, "ProcedureCodeSequence");
        // ensureInAttributeMetadata(TagFromName.ProcedureContextFlagTrial,
        // "ProcedureContextFlagTrial");
        // ensureInAttributeMetadata(TagFromName.ProcedureContextSequenceTrial,
        // "ProcedureContextSequenceTrial");
        // ensureInAttributeMetadata(TagFromName.ProcedureIdentifierCodeSequenceTrial,
        // "ProcedureIdentifierCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.ProcessingFunction, "ProcessingFunction");
        ensureInAttributeMetadata(TagFromName.ProductDescription, "ProductDescription");
        ensureInAttributeMetadata(TagFromName.ProductExpirationDateTime, "ProductExpirationDateTime");
        ensureInAttributeMetadata(TagFromName.ProductLotIdentifier, "ProductLotIdentifier");
        ensureInAttributeMetadata(TagFromName.ProductName, "ProductName");
        ensureInAttributeMetadata(TagFromName.ProductPackageIdentifier, "ProductPackageIdentifier");
        ensureInAttributeMetadata(TagFromName.ProductParameterSequence, "ProductParameterSequence");
        ensureInAttributeMetadata(TagFromName.ProductTypeCodeSequence, "ProductTypeCodeSequence");
        ensureInAttributeMetadata(TagFromName.ProjectionEponymousNameCodeSequence,
                "ProjectionEponymousNameCodeSequence");
        ensureInAttributeMetadata(TagFromName.ProjectionPixelCalibrationSequence, "ProjectionPixelCalibrationSequence");
        ensureInAttributeMetadata(TagFromName.ProposedStudySequence, "ProposedStudySequence");
        ensureInAttributeMetadata(TagFromName.ProtocolContextSequence, "ProtocolContextSequence");
        ensureInAttributeMetadata(TagFromName.ProtocolName, "ProtocolName");
        ensureInAttributeMetadata(TagFromName.PseudocolorType, "PseudocolorType");
        ensureInAttributeMetadata(TagFromName.PulseRepetitionFrequency, "PulseRepetitionFrequency");
        ensureInAttributeMetadata(TagFromName.PulseRepetitionInterval, "PulseRepetitionInterval");
        ensureInAttributeMetadata(TagFromName.PulseSequenceName, "PulseSequenceName");
        ensureInAttributeMetadata(TagFromName.PupilDilated, "PupilDilated");
        ensureInAttributeMetadata(TagFromName.PurposeOfReferenceCodeSequence, "PurposeOfReferenceCodeSequence");
        ensureInAttributeMetadata(TagFromName.PVCRejection, "PVCRejection");
        ensureInAttributeMetadata(TagFromName.QuadratureReceiveCoil, "QuadratureReceiveCoil");
        ensureInAttributeMetadata(TagFromName.QualityControlImage, "QualityControlImage");
        ensureInAttributeMetadata(TagFromName.Quantity, "Quantity");
        ensureInAttributeMetadata(TagFromName.QuantitySequence, "QuantitySequence");
        ensureInAttributeMetadata(TagFromName.QueryRetrieveLevel, "QueryRetrieveLevel");
        ensureInAttributeMetadata(TagFromName.QueueStatus, "QueueStatus");
        ensureInAttributeMetadata(TagFromName.RadialPosition, "RadialPosition");
        ensureInAttributeMetadata(TagFromName.RadiationAtomicNumber, "RadiationAtomicNumber");
        ensureInAttributeMetadata(TagFromName.RadiationChargeState, "RadiationChargeState");
        ensureInAttributeMetadata(TagFromName.RadiationMachineName, "RadiationMachineName");
        ensureInAttributeMetadata(TagFromName.RadiationMachineSAD, "RadiationMachineSAD");
        ensureInAttributeMetadata(TagFromName.RadiationMachineSSD, "RadiationMachineSSD");
        ensureInAttributeMetadata(TagFromName.RadiationMassNumber, "RadiationMassNumber");
        ensureInAttributeMetadata(TagFromName.RadiationMode, "RadiationMode");
        ensureInAttributeMetadata(TagFromName.RadiationSetting, "RadiationSetting");
        ensureInAttributeMetadata(TagFromName.RadiationType, "RadiationType");
        ensureInAttributeMetadata(TagFromName.Radionuclide, "Radionuclide");
        ensureInAttributeMetadata(TagFromName.RadionuclideCodeSequence, "RadionuclideCodeSequence");
        ensureInAttributeMetadata(TagFromName.RadionuclideHalfLife, "RadionuclideHalfLife");
        ensureInAttributeMetadata(TagFromName.RadionuclidePositronFraction, "RadionuclidePositronFraction");
        ensureInAttributeMetadata(TagFromName.RadionuclideTotalDose, "RadionuclideTotalDose");
        ensureInAttributeMetadata(TagFromName.Radiopharmaceutical, "Radiopharmaceutical");
        ensureInAttributeMetadata(TagFromName.RadiopharmaceuticalCodeSequence, "RadiopharmaceuticalCodeSequence");
        ensureInAttributeMetadata(TagFromName.RadiopharmaceuticalInformationSequence,
                "RadiopharmaceuticalInformationSequence");
        ensureInAttributeMetadata(TagFromName.RadiopharmaceuticalRoute, "RadiopharmaceuticalRoute");
        ensureInAttributeMetadata(TagFromName.RadiopharmaceuticalSpecificActivity,
                "RadiopharmaceuticalSpecificActivity");
        ensureInAttributeMetadata(TagFromName.RadiopharmaceuticalStartDateTime, "RadiopharmaceuticalStartDateTime");
        ensureInAttributeMetadata(TagFromName.RadiopharmaceuticalStartTime, "RadiopharmaceuticalStartTime");
        ensureInAttributeMetadata(TagFromName.RadiopharmaceuticalStopDateTime, "RadiopharmaceuticalStopDateTime");
        ensureInAttributeMetadata(TagFromName.RadiopharmaceuticalStopTime, "RadiopharmaceuticalStopTime");
        ensureInAttributeMetadata(TagFromName.RadiopharmaceuticalVolume, "RadiopharmaceuticalVolume");
        ensureInAttributeMetadata(TagFromName.RadiusOfCircularCollimator, "RadiusOfCircularCollimator");
        ensureInAttributeMetadata(TagFromName.RadiusOfCircularExposureControlSensingRegion,
                "RadiusOfCircularExposureControlSensingRegion");
        ensureInAttributeMetadata(TagFromName.RadiusOfCircularShutter, "RadiusOfCircularShutter");
        ensureInAttributeMetadata(TagFromName.RandomsCorrectionMethod, "RandomsCorrectionMethod");
        ensureInAttributeMetadata(TagFromName.RangeModulatorDescription, "RangeModulatorDescription");
        ensureInAttributeMetadata(TagFromName.RangeModulatorGatingStartValue, "RangeModulatorGatingStartValue");
        ensureInAttributeMetadata(TagFromName.RangeModulatorGatingStartWaterEquivalentThickness,
                "RangeModulatorGatingStartWaterEquivalentThickness");
        ensureInAttributeMetadata(TagFromName.RangeModulatorGatingStopValue, "RangeModulatorGatingStopValue");
        ensureInAttributeMetadata(TagFromName.RangeModulatorGatingStopWaterEquivalentThickness,
                "RangeModulatorGatingStopWaterEquivalentThickness");
        ensureInAttributeMetadata(TagFromName.RangeModulatorID, "RangeModulatorID");
        ensureInAttributeMetadata(TagFromName.RangeModulatorNumber, "RangeModulatorNumber");
        ensureInAttributeMetadata(TagFromName.RangeModulatorSequence, "RangeModulatorSequence");
        ensureInAttributeMetadata(TagFromName.RangeModulatorSettingsSequence, "RangeModulatorSettingsSequence");
        ensureInAttributeMetadata(TagFromName.RangeModulatorType, "RangeModulatorType");
        ensureInAttributeMetadata(TagFromName.RangeShifterDescription, "RangeShifterDescription");
        ensureInAttributeMetadata(TagFromName.RangeShifterID, "RangeShifterID");
        ensureInAttributeMetadata(TagFromName.RangeShifterNumber, "RangeShifterNumber");
        ensureInAttributeMetadata(TagFromName.RangeShifterSequence, "RangeShifterSequence");
        ensureInAttributeMetadata(TagFromName.RangeShifterSetting, "RangeShifterSetting");
        ensureInAttributeMetadata(TagFromName.RangeShifterSettingsSequence, "RangeShifterSettingsSequence");
        ensureInAttributeMetadata(TagFromName.RangeShifterType, "RangeShifterType");
        ensureInAttributeMetadata(TagFromName.RangeShifterWaterEquivalentThickness,
                "RangeShifterWaterEquivalentThickness");
        ensureInAttributeMetadata(TagFromName.RealWorldValueFirstValueMapped, "RealWorldValueFirstValueMapped");
        ensureInAttributeMetadata(TagFromName.RealWorldValueIntercept, "RealWorldValueIntercept");
        ensureInAttributeMetadata(TagFromName.RealWorldValueLastValueMapped, "RealWorldValueLastValueMapped");
        ensureInAttributeMetadata(TagFromName.RealWorldValueLUTData, "RealWorldValueLUTData");
        ensureInAttributeMetadata(TagFromName.RealWorldValueMappingSequence, "RealWorldValueMappingSequence");
        ensureInAttributeMetadata(TagFromName.RealWorldValueSlope, "RealWorldValueSlope");
        ensureInAttributeMetadata(TagFromName.ReasonForImagingServiceRequest, "ReasonForImagingServiceRequest");
        ensureInAttributeMetadata(TagFromName.ReasonForRequestedProcedure, "ReasonForRequestedProcedure");
        ensureInAttributeMetadata(TagFromName.ReasonForRequestedProcedureCodeSequence,
                "ReasonForRequestedProcedureCodeSequence");
        ensureInAttributeMetadata(TagFromName.ReasonForStudy, "ReasonForStudy");
        ensureInAttributeMetadata(TagFromName.ReasonForTheAttributeModification, "ReasonForTheAttributeModification");
        ensureInAttributeMetadata(TagFromName.ReceiveCoilManufacturerName, "ReceiveCoilManufacturerName");
        ensureInAttributeMetadata(TagFromName.ReceiveCoilName, "ReceiveCoilName");
        ensureInAttributeMetadata(TagFromName.ReceiveCoilType, "ReceiveCoilType");
        ensureInAttributeMetadata(TagFromName.Receiver, "Receiver");
        ensureInAttributeMetadata(TagFromName.RecognitionCode, "RecognitionCode");
        ensureInAttributeMetadata(TagFromName.RecommendedDisplayCIELabValue, "RecommendedDisplayCIELabValue");
        ensureInAttributeMetadata(TagFromName.RecommendedDisplayFrameRate, "RecommendedDisplayFrameRate");
        ensureInAttributeMetadata(TagFromName.RecommendedDisplayFrameRateInFloat, "RecommendedDisplayFrameRateInFloat");
        ensureInAttributeMetadata(TagFromName.RecommendedDisplayGrayscaleValue, "RecommendedDisplayGrayscaleValue");
        ensureInAttributeMetadata(TagFromName.RecommendedViewingMode, "RecommendedViewingMode");
        ensureInAttributeMetadata(TagFromName.ReconstructionAlgorithm, "ReconstructionAlgorithm");
        ensureInAttributeMetadata(TagFromName.ReconstructionAngle, "ReconstructionAngle");
        ensureInAttributeMetadata(TagFromName.ReconstructionDescription, "ReconstructionDescription");
        ensureInAttributeMetadata(TagFromName.ReconstructionDiameter, "ReconstructionDiameter");
        ensureInAttributeMetadata(TagFromName.ReconstructionFieldOfView, "ReconstructionFieldOfView");
        ensureInAttributeMetadata(TagFromName.ReconstructionIndex, "ReconstructionIndex");
        ensureInAttributeMetadata(TagFromName.ReconstructionMethod, "ReconstructionMethod");
        ensureInAttributeMetadata(TagFromName.ReconstructionPixelSpacing, "ReconstructionPixelSpacing");
        ensureInAttributeMetadata(TagFromName.ReconstructionTargetCenterPatient, "ReconstructionTargetCenterPatient");
        ensureInAttributeMetadata(TagFromName.RecordedBlockSequence, "RecordedBlockSequence");
        ensureInAttributeMetadata(TagFromName.RecordedBrachyAccessoryDeviceSequence,
                "RecordedBrachyAccessoryDeviceSequence");
        ensureInAttributeMetadata(TagFromName.RecordedChannelSequence, "RecordedChannelSequence");
        ensureInAttributeMetadata(TagFromName.RecordedChannelShieldSequence, "RecordedChannelShieldSequence");
        ensureInAttributeMetadata(TagFromName.RecordedCompensatorSequence, "RecordedCompensatorSequence");
        ensureInAttributeMetadata(TagFromName.RecordedLateralSpreadingDeviceSequence,
                "RecordedLateralSpreadingDeviceSequence");
        ensureInAttributeMetadata(TagFromName.RecordedRangeModulatorSequence, "RecordedRangeModulatorSequence");
        ensureInAttributeMetadata(TagFromName.RecordedRangeShifterSequence, "RecordedRangeShifterSequence");
        ensureInAttributeMetadata(TagFromName.RecordedSnoutSequence, "RecordedSnoutSequence");
        ensureInAttributeMetadata(TagFromName.RecordedSourceApplicatorSequence, "RecordedSourceApplicatorSequence");
        ensureInAttributeMetadata(TagFromName.RecordedSourceSequence, "RecordedSourceSequence");
        ensureInAttributeMetadata(TagFromName.RecordedWedgeSequence, "RecordedWedgeSequence");
        ensureInAttributeMetadata(TagFromName.RecordInUseFlag, "RecordInUseFlag");
        ensureInAttributeMetadata(TagFromName.RectificationType, "RectificationType");
        ensureInAttributeMetadata(TagFromName.RectilinearPhaseEncodeReordering, "RectilinearPhaseEncodeReordering");
        ensureInAttributeMetadata(TagFromName.RedPaletteColorLookupTableData, "RedPaletteColorLookupTableData");
        ensureInAttributeMetadata(TagFromName.RedPaletteColorLookupTableDescriptor,
                "RedPaletteColorLookupTableDescriptor");
        ensureInAttributeMetadata(TagFromName.Reference, "Reference");
        ensureInAttributeMetadata(TagFromName.ReferenceAirKermaRate, "ReferenceAirKermaRate");
        ensureInAttributeMetadata(TagFromName.ReferenceCoordinates, "ReferenceCoordinates");
        // ensureInAttributeMetadata(TagFromName.ReferencedAccessionSequenceTrial,
        // "ReferencedAccessionSequenceTrial");
        ensureInAttributeMetadata(TagFromName.ReferencedBasicAnnotationBoxSequence,
                "ReferencedBasicAnnotationBoxSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedBeamNumber, "ReferencedBeamNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedBeamSequence, "ReferencedBeamSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedBlockNumber, "ReferencedBlockNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedBolusSequence, "ReferencedBolusSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedBrachyAccessoryDeviceNumber,
                "ReferencedBrachyAccessoryDeviceNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedBrachyApplicationSetupNumber,
                "ReferencedBrachyApplicationSetupNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedBrachyApplicationSetupSequence,
                "ReferencedBrachyApplicationSetupSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedCalculatedDoseReferenceNumber,
                "ReferencedCalculatedDoseReferenceNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedCalculatedDoseReferenceSequence,
                "ReferencedCalculatedDoseReferenceSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedChannelShieldNumber, "ReferencedChannelShieldNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedCompensatorNumber, "ReferencedCompensatorNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedContentItemIdentifier, "ReferencedContentItemIdentifier");
        ensureInAttributeMetadata(TagFromName.ReferencedControlPointIndex, "ReferencedControlPointIndex");
        ensureInAttributeMetadata(TagFromName.ReferencedControlPointSequence, "ReferencedControlPointSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedCurveSequence, "ReferencedCurveSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedDateTime, "ReferencedDateTime");
        ensureInAttributeMetadata(TagFromName.ReferencedDigitalSignatureSequence, "ReferencedDigitalSignatureSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedDoseReferenceNumber, "ReferencedDoseReferenceNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedDoseReferenceSequence, "ReferencedDoseReferenceSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedDoseSequence, "ReferencedDoseSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedFileID, "ReferencedFileID");
        ensureInAttributeMetadata(TagFromName.ReferencedFilmBoxSequence, "ReferencedFilmBoxSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedFilmSessionSequence, "ReferencedFilmSessionSequence");
        // ensureInAttributeMetadata(TagFromName.ReferencedFindingsGroupUIDTrial,
        // "ReferencedFindingsGroupUIDTrial");
        ensureInAttributeMetadata(TagFromName.ReferencedFractionGroupNumber, "ReferencedFractionGroupNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedFractionGroupSequence, "ReferencedFractionGroupSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedFractionNumber, "ReferencedFractionNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedFrameNumber, "ReferencedFrameNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedFrameNumbers, "ReferencedFrameNumbers");
        ensureInAttributeMetadata(TagFromName.ReferencedFrameOfReferenceSequence, "ReferencedFrameOfReferenceSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedFrameOfReferenceUID, "ReferencedFrameOfReferenceUID");
        ensureInAttributeMetadata(TagFromName.ReferencedGeneralPurposeScheduledProcedureStepSequence,
                "ReferencedGeneralPurposeScheduledProcedureStepSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedGeneralPurposeScheduledProcedureStepTransactionUID,
                "ReferencedGeneralPurposeScheduledProcedureStepTransactionUID");
        ensureInAttributeMetadata(TagFromName.ReferencedGrayscalePresentationStateSequence,
                "ReferencedGrayscalePresentationStateSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedImageBoxSequence, "ReferencedImageBoxSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedImageEvidenceSequence, "ReferencedImageEvidenceSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedImageOverlayBoxSequence, "ReferencedImageOverlayBoxSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedImageRealWorldValueMappingSequence,
                "ReferencedImageRealWorldValueMappingSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedImageSequence, "ReferencedImageSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedInstanceSequence, "ReferencedInstanceSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedInterpretationSequence, "ReferencedInterpretationSequence");
        ensureInAttributeMetadata(TagFromName.ReferenceDisplaySets, "ReferenceDisplaySets");
        ensureInAttributeMetadata(TagFromName.ReferencedLateralSpreadingDeviceNumber,
                "ReferencedLateralSpreadingDeviceNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedMeasuredDoseReferenceNumber,
                "ReferencedMeasuredDoseReferenceNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedMeasuredDoseReferenceSequence,
                "ReferencedMeasuredDoseReferenceSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedNonImageCompositeSOPInstanceSequence,
                "ReferencedNonImageCompositeSOPInstanceSequence");
        // ensureInAttributeMetadata(TagFromName.ReferencedObjectObservationClassTrial,
        // "ReferencedObjectObservationClassTrial");
        // ensureInAttributeMetadata(TagFromName.ReferencedObservationClassTrial,
        // "ReferencedObservationClassTrial");
        // ensureInAttributeMetadata(TagFromName.ReferencedObservationUIDTrial,
        // "ReferencedObservationUIDTrial");
        ensureInAttributeMetadata(TagFromName.ReferencedOtherPlaneSequence, "ReferencedOtherPlaneSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedOverlayImageBoxSequence, "ReferencedOverlayImageBoxSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedOverlayPlaneGroups, "ReferencedOverlayPlaneGroups");
        ensureInAttributeMetadata(TagFromName.ReferencedOverlayPlaneSequence, "ReferencedOverlayPlaneSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedOverlaySequence, "ReferencedOverlaySequence");
        ensureInAttributeMetadata(TagFromName.ReferencedPatientAliasSequence, "ReferencedPatientAliasSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedPatientSequence, "ReferencedPatientSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedPatientSetupNumber, "ReferencedPatientSetupNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedPerformedProcedureStepSequence,
                "ReferencedPerformedProcedureStepSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedPresentationLUTSequence, "ReferencedPresentationLUTSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedPrintJobSequencePull, "ReferencedPrintJobSequencePull");
        ensureInAttributeMetadata(TagFromName.ReferencedPrintJobSequenceQueue, "ReferencedPrintJobSequenceQueue");
        ensureInAttributeMetadata(TagFromName.ReferencedProcedureStepSequence, "ReferencedProcedureStepSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedRangeModulatorNumber, "ReferencedRangeModulatorNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedRangeShifterNumber, "ReferencedRangeShifterNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedRawDataSequence, "ReferencedRawDataSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedRealWorldValueMappingInstanceSequence,
                "ReferencedRealWorldValueMappingInstanceSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedReferenceImageNumber, "ReferencedReferenceImageNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedReferenceImageSequence, "ReferencedReferenceImageSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedRelatedGeneralSOPClassUIDInFile,
                "ReferencedRelatedGeneralSOPClassUIDInFile");
        ensureInAttributeMetadata(TagFromName.ReferencedRequestSequence, "ReferencedRequestSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedResultsSequence, "ReferencedResultsSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedROINumber, "ReferencedROINumber");
        ensureInAttributeMetadata(TagFromName.ReferencedRTPlanSequence, "ReferencedRTPlanSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedSamplePositions, "ReferencedSamplePositions");
        ensureInAttributeMetadata(TagFromName.ReferencedSegmentNumber, "ReferencedSegmentNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedSeriesSequence, "ReferencedSeriesSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedSetupImageSequence, "ReferencedSetupImageSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedSOPClassUID, "ReferencedSOPClassUID");
        ensureInAttributeMetadata(TagFromName.ReferencedSOPClassUIDInFile, "ReferencedSOPClassUIDInFile");
        ensureInAttributeMetadata(TagFromName.ReferencedSOPInstanceMACSequence, "ReferencedSOPInstanceMACSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedSOPInstanceUID, "ReferencedSOPInstanceUID");
        ensureInAttributeMetadata(TagFromName.ReferencedSOPInstanceUIDInFile, "ReferencedSOPInstanceUIDInFile");
        ensureInAttributeMetadata(TagFromName.ReferencedSOPSequence, "ReferencedSOPSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedSourceApplicatorNumber, "ReferencedSourceApplicatorNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedSourceNumber, "ReferencedSourceNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedSpatialRegistrationSequence,
                "ReferencedSpatialRegistrationSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedStartControlPointIndex, "ReferencedStartControlPointIndex");
        ensureInAttributeMetadata(TagFromName.ReferencedStopControlPointIndex, "ReferencedStopControlPointIndex");
        ensureInAttributeMetadata(TagFromName.ReferencedStorageMediaSequence, "ReferencedStorageMediaSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedStoredPrintSequence, "ReferencedStoredPrintSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedStructureSetSequence, "ReferencedStructureSetSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedStudySequence, "ReferencedStudySequence");
        ensureInAttributeMetadata(TagFromName.ReferencedTimeOffsets, "ReferencedTimeOffsets");
        ensureInAttributeMetadata(TagFromName.ReferencedToleranceTableNumber, "ReferencedToleranceTableNumber");
        ensureInAttributeMetadata(TagFromName.ReferencedTransferSyntaxUIDInFile, "ReferencedTransferSyntaxUIDInFile");
        ensureInAttributeMetadata(TagFromName.ReferencedTreatmentRecordSequence, "ReferencedTreatmentRecordSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedVerificationImageSequence,
                "ReferencedVerificationImageSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedVisitSequence, "ReferencedVisitSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedVOILUTBoxSequence, "ReferencedVOILUTBoxSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedWaveformChannels, "ReferencedWaveformChannels");
        ensureInAttributeMetadata(TagFromName.ReferencedWaveformSequence, "ReferencedWaveformSequence");
        ensureInAttributeMetadata(TagFromName.ReferencedWedgeNumber, "ReferencedWedgeNumber");
        ensureInAttributeMetadata(TagFromName.ReferenceImageNumber, "ReferenceImageNumber");
        ensureInAttributeMetadata(TagFromName.ReferencePixelPhysicalValueX, "ReferencePixelPhysicalValueX");
        ensureInAttributeMetadata(TagFromName.ReferencePixelPhysicalValueY, "ReferencePixelPhysicalValueY");
        ensureInAttributeMetadata(TagFromName.ReferencePixelX0, "ReferencePixelX0");
        ensureInAttributeMetadata(TagFromName.ReferencePixelY0, "ReferencePixelY0");
        ensureInAttributeMetadata(TagFromName.ReferenceToRecordedSound, "ReferenceToRecordedSound");
        ensureInAttributeMetadata(TagFromName.ReferringPhysicianAddress, "ReferringPhysicianAddress");
        ensureInAttributeMetadata(TagFromName.ReferringPhysicianIdentificationSequence,
                "ReferringPhysicianIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.ReferringPhysicianName, "ReferringPhysicianName");
        ensureInAttributeMetadata(TagFromName.ReferringPhysicianTelephoneNumber, "ReferringPhysicianTelephoneNumber");
        ensureInAttributeMetadata(TagFromName.ReflectedAmbientLight, "ReflectedAmbientLight");
        ensureInAttributeMetadata(TagFromName.ReformattingInterval, "ReformattingInterval");
        ensureInAttributeMetadata(TagFromName.ReformattingOperationInitialViewDirection,
                "ReformattingOperationInitialViewDirection");
        ensureInAttributeMetadata(TagFromName.ReformattingOperationType, "ReformattingOperationType");
        ensureInAttributeMetadata(TagFromName.ReformattingThickness, "ReformattingThickness");
        ensureInAttributeMetadata(TagFromName.RefractiveStateSequence, "RefractiveStateSequence");
        ensureInAttributeMetadata(TagFromName.RegionDataType, "RegionDataType");
        ensureInAttributeMetadata(TagFromName.RegionFlags, "RegionFlags");
        ensureInAttributeMetadata(TagFromName.RegionLocationMaxX1, "RegionLocationMaxX1");
        ensureInAttributeMetadata(TagFromName.RegionLocationMaxY1, "RegionLocationMaxY1");
        ensureInAttributeMetadata(TagFromName.RegionLocationMinX0, "RegionLocationMinX0");
        ensureInAttributeMetadata(TagFromName.RegionLocationMinY0, "RegionLocationMinY0");
        ensureInAttributeMetadata(TagFromName.RegionOfResidence, "RegionOfResidence");
        ensureInAttributeMetadata(TagFromName.RegionSpatialFormat, "RegionSpatialFormat");
        ensureInAttributeMetadata(TagFromName.RegistrationSequence, "RegistrationSequence");
        ensureInAttributeMetadata(TagFromName.RegistrationTypeCodeSequence, "RegistrationTypeCodeSequence");
        ensureInAttributeMetadata(TagFromName.RelatedFrameOfReferenceUID, "RelatedFrameOfReferenceUID");
        ensureInAttributeMetadata(TagFromName.RelatedGeneralSOPClassUID, "RelatedGeneralSOPClassUID");
        ensureInAttributeMetadata(TagFromName.RelatedRTROIObservationsSequence, "RelatedRTROIObservationsSequence");
        ensureInAttributeMetadata(TagFromName.RelatedSeriesSequence, "RelatedSeriesSequence");
        // ensureInAttributeMetadata(TagFromName.RelationshipSequenceTrial,
        // "RelationshipSequenceTrial");
        ensureInAttributeMetadata(TagFromName.RelationshipType, "RelationshipType");
        // ensureInAttributeMetadata(TagFromName.RelationshipTypeCodeSequenceModifierTrial,
        // "RelationshipTypeCodeSequenceModifierTrial");
        // ensureInAttributeMetadata(TagFromName.RelationshipTypeCodeSequenceTrial,
        // "RelationshipTypeCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.RelativeImagePositionCodeSequence, "RelativeImagePositionCodeSequence");
        ensureInAttributeMetadata(TagFromName.RelativeOpacity, "RelativeOpacity");
        ensureInAttributeMetadata(TagFromName.RelativeTime, "RelativeTime");
        ensureInAttributeMetadata(TagFromName.RelativeTimeUnits, "RelativeTimeUnits");
        ensureInAttributeMetadata(TagFromName.RelativeXRayExposure, "RelativeXRayExposure");
        ensureInAttributeMetadata(TagFromName.RelevantInformationSequence, "RelevantInformationSequence");
        ensureInAttributeMetadata(TagFromName.RepeatFractionCycleLength, "RepeatFractionCycleLength");
        ensureInAttributeMetadata(TagFromName.RepeatInterval, "RepeatInterval");
        ensureInAttributeMetadata(TagFromName.RepetitionTime, "RepetitionTime");
        // ensureInAttributeMetadata(TagFromName.ReportDetailSequenceTrial,
        // "ReportDetailSequenceTrial");
        ensureInAttributeMetadata(TagFromName.ReportedValuesOrigin, "ReportedValuesOrigin");
        ensureInAttributeMetadata(TagFromName.ReportingPriority, "ReportingPriority");
        // ensureInAttributeMetadata(TagFromName.ReportNumberTrial,
        // "ReportNumberTrial");
        // ensureInAttributeMetadata(TagFromName.ReportProductionStatusTrial,
        // "ReportProductionStatusTrial");
        // ensureInAttributeMetadata(TagFromName.ReportStatusCommentTrial,
        // "ReportStatusCommentTrial");
        // ensureInAttributeMetadata(TagFromName.ReportStatusIDTrial,
        // "ReportStatusIDTrial");
        ensureInAttributeMetadata(TagFromName.RepresentativeFrameNumber, "RepresentativeFrameNumber");
        ensureInAttributeMetadata(TagFromName.ReprojectionMethod, "ReprojectionMethod");
        ensureInAttributeMetadata(TagFromName.RequestAttributesSequence, "RequestAttributesSequence");
        ensureInAttributeMetadata(TagFromName.RequestedContrastAgent, "RequestedContrastAgent");
        ensureInAttributeMetadata(TagFromName.RequestedDecimateCropBehavior, "RequestedDecimateCropBehavior");
        ensureInAttributeMetadata(TagFromName.RequestedImageSize, "RequestedImageSize");
        ensureInAttributeMetadata(TagFromName.RequestedImageSizeFlag, "RequestedImageSizeFlag");
        ensureInAttributeMetadata(TagFromName.RequestedMediaApplicationProfile, "RequestedMediaApplicationProfile");
        ensureInAttributeMetadata(TagFromName.RequestedProcedureCodeSequence, "RequestedProcedureCodeSequence");
        ensureInAttributeMetadata(TagFromName.RequestedProcedureComments, "RequestedProcedureComments");
        ensureInAttributeMetadata(TagFromName.RequestedProcedureDescription, "RequestedProcedureDescription");
        ensureInAttributeMetadata(TagFromName.RequestedProcedureID, "RequestedProcedureID");
        ensureInAttributeMetadata(TagFromName.RequestedProcedureLocation, "RequestedProcedureLocation");
        ensureInAttributeMetadata(TagFromName.RequestedProcedurePriority, "RequestedProcedurePriority");
        ensureInAttributeMetadata(TagFromName.RequestedResolutionID, "RequestedResolutionID");
        ensureInAttributeMetadata(TagFromName.RequestedSOPClassUID, "RequestedSOPClassUID");
        ensureInAttributeMetadata(TagFromName.RequestedSOPInstanceUID, "RequestedSOPInstanceUID");
        ensureInAttributeMetadata(TagFromName.RequestedSubsequentWorkitemCodeSequence,
                "RequestedSubsequentWorkitemCodeSequence");
        ensureInAttributeMetadata(TagFromName.RequestingPhysician, "RequestingPhysician");
        ensureInAttributeMetadata(TagFromName.RequestingPhysicianIdentificationSequence,
                "RequestingPhysicianIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.RequestingService, "RequestingService");
        ensureInAttributeMetadata(TagFromName.RequestPriority, "RequestPriority");
        ensureInAttributeMetadata(TagFromName.RescaleIntercept, "RescaleIntercept");
        ensureInAttributeMetadata(TagFromName.RescaleSlope, "RescaleSlope");
        ensureInAttributeMetadata(TagFromName.RescaleType, "RescaleType");
        ensureInAttributeMetadata(TagFromName.ResidualSyringeCounts, "ResidualSyringeCounts");
        ensureInAttributeMetadata(TagFromName.ResonantNucleus, "ResonantNucleus");
        ensureInAttributeMetadata(TagFromName.RespiratoryCyclePosition, "RespiratoryCyclePosition");
        ensureInAttributeMetadata(TagFromName.RespiratoryIntervalTime, "RespiratoryIntervalTime");
        ensureInAttributeMetadata(TagFromName.RespiratoryMotionCompensationTechnique,
                "RespiratoryMotionCompensationTechnique");
        ensureInAttributeMetadata(TagFromName.RespiratoryMotionCompensationTechniqueDescription,
                "RespiratoryMotionCompensationTechniqueDescription");
        ensureInAttributeMetadata(TagFromName.RespiratorySignalSource, "RespiratorySignalSource");
        ensureInAttributeMetadata(TagFromName.RespiratorySignalSourceID, "RespiratorySignalSourceID");
        ensureInAttributeMetadata(TagFromName.RespiratorySynchronizationSequence, "RespiratorySynchronizationSequence");
        ensureInAttributeMetadata(TagFromName.RespiratoryTriggerDelayThreshold, "RespiratoryTriggerDelayThreshold");
        ensureInAttributeMetadata(TagFromName.RespiratoryTriggerType, "RespiratoryTriggerType");
        ensureInAttributeMetadata(TagFromName.ResponseSequenceNumber, "ResponseSequenceNumber");
        ensureInAttributeMetadata(TagFromName.ResponsibleOrganization, "ResponsibleOrganization");
        ensureInAttributeMetadata(TagFromName.ResponsiblePerson, "ResponsiblePerson");
        ensureInAttributeMetadata(TagFromName.ResponsiblePersonRole, "ResponsiblePersonRole");
        ensureInAttributeMetadata(TagFromName.ResultingGeneralPurposePerformedProcedureStepsSequence,
                "ResultingGeneralPurposePerformedProcedureStepsSequence");
        ensureInAttributeMetadata(TagFromName.ResultsComments, "ResultsComments");
        ensureInAttributeMetadata(TagFromName.ResultsDistributionListSequence, "ResultsDistributionListSequence");
        ensureInAttributeMetadata(TagFromName.ResultsID, "ResultsID");
        ensureInAttributeMetadata(TagFromName.ResultsIDIssuer, "ResultsIDIssuer");
        ensureInAttributeMetadata(TagFromName.RetrieveAETitle, "RetrieveAETitle");
        ensureInAttributeMetadata(TagFromName.RetrieveURI, "RetrieveURI");
        ensureInAttributeMetadata(TagFromName.ReviewDate, "ReviewDate");
        ensureInAttributeMetadata(TagFromName.ReviewerName, "ReviewerName");
        ensureInAttributeMetadata(TagFromName.ReviewTime, "ReviewTime");
        ensureInAttributeMetadata(TagFromName.RevolutionTime, "RevolutionTime");
        ensureInAttributeMetadata(TagFromName.RFEchoTrainLength, "RFEchoTrainLength");
        ensureInAttributeMetadata(TagFromName.RightImageSequence, "RightImageSequence");
        ensureInAttributeMetadata(TagFromName.ROIArea, "ROIArea");
        ensureInAttributeMetadata(TagFromName.ROIContourSequence, "ROIContourSequence");
        ensureInAttributeMetadata(TagFromName.ROIDescription, "ROIDescription");
        ensureInAttributeMetadata(TagFromName.ROIDisplayColor, "ROIDisplayColor");
        ensureInAttributeMetadata(TagFromName.ROIElementalCompositionAtomicMassFraction,
                "ROIElementalCompositionAtomicMassFraction");
        ensureInAttributeMetadata(TagFromName.ROIElementalCompositionAtomicNumber,
                "ROIElementalCompositionAtomicNumber");
        ensureInAttributeMetadata(TagFromName.ROIElementalCompositionSequence, "ROIElementalCompositionSequence");
        ensureInAttributeMetadata(TagFromName.ROIGenerationAlgorithm, "ROIGenerationAlgorithm");
        ensureInAttributeMetadata(TagFromName.ROIGenerationDescription, "ROIGenerationDescription");
        ensureInAttributeMetadata(TagFromName.ROIInterpreter, "ROIInterpreter");
        ensureInAttributeMetadata(TagFromName.ROIMean, "ROIMean");
        ensureInAttributeMetadata(TagFromName.ROIName, "ROIName");
        ensureInAttributeMetadata(TagFromName.ROINumber, "ROINumber");
        ensureInAttributeMetadata(TagFromName.ROIObservationDescription, "ROIObservationDescription");
        ensureInAttributeMetadata(TagFromName.ROIObservationLabel, "ROIObservationLabel");
        ensureInAttributeMetadata(TagFromName.ROIPhysicalPropertiesSequence, "ROIPhysicalPropertiesSequence");
        ensureInAttributeMetadata(TagFromName.ROIPhysicalProperty, "ROIPhysicalProperty");
        ensureInAttributeMetadata(TagFromName.ROIPhysicalPropertyValue, "ROIPhysicalPropertyValue");
        ensureInAttributeMetadata(TagFromName.ROIStandardDeviation, "ROIStandardDeviation");
        ensureInAttributeMetadata(TagFromName.ROIVolume, "ROIVolume");
        ensureInAttributeMetadata(TagFromName.RootDirectoryFirstRecord, "RootDirectoryFirstRecord");
        ensureInAttributeMetadata(TagFromName.RootDirectoryLastRecord, "RootDirectoryLastRecord");
        ensureInAttributeMetadata(TagFromName.RotationDirection, "RotationDirection");
        ensureInAttributeMetadata(TagFromName.RotationInformationSequence, "RotationInformationSequence");
        ensureInAttributeMetadata(TagFromName.RotationOffset, "RotationOffset");
        ensureInAttributeMetadata(TagFromName.RotationOfScannedFilm, "RotationOfScannedFilm");
        ensureInAttributeMetadata(TagFromName.RotationVector, "RotationVector");
        ensureInAttributeMetadata(TagFromName.RouteOfAdmissions, "RouteOfAdmissions");
        ensureInAttributeMetadata(TagFromName.RowOverlap, "RowOverlap");
        ensureInAttributeMetadata(TagFromName.Rows, "Rows");
        ensureInAttributeMetadata(TagFromName.RowsForNthOrderCoefficients, "RowsForNthOrderCoefficients");
        ensureInAttributeMetadata(TagFromName.RRIntervalTimeNominal, "RRIntervalTimeNominal");
        ensureInAttributeMetadata(TagFromName.RRIntervalVector, "RRIntervalVector");
        ensureInAttributeMetadata(TagFromName.RTBeamLimitingDeviceType, "RTBeamLimitingDeviceType");
        ensureInAttributeMetadata(TagFromName.RTDoseROISequence, "RTDoseROISequence");
        ensureInAttributeMetadata(TagFromName.RTImageDescription, "RTImageDescription");
        ensureInAttributeMetadata(TagFromName.RTImageLabel, "RTImageLabel");
        ensureInAttributeMetadata(TagFromName.RTImageName, "RTImageName");
        ensureInAttributeMetadata(TagFromName.RTImageOrientation, "RTImageOrientation");
        ensureInAttributeMetadata(TagFromName.RTImagePlane, "RTImagePlane");
        ensureInAttributeMetadata(TagFromName.RTImagePosition, "RTImagePosition");
        ensureInAttributeMetadata(TagFromName.RTImageSID, "RTImageSID");
        ensureInAttributeMetadata(TagFromName.RTPlanDate, "RTPlanDate");
        ensureInAttributeMetadata(TagFromName.RTPlanDescription, "RTPlanDescription");
        ensureInAttributeMetadata(TagFromName.RTPlanGeometry, "RTPlanGeometry");
        ensureInAttributeMetadata(TagFromName.RTPlanLabel, "RTPlanLabel");
        ensureInAttributeMetadata(TagFromName.RTPlanName, "RTPlanName");
        ensureInAttributeMetadata(TagFromName.RTPlanRelationship, "RTPlanRelationship");
        ensureInAttributeMetadata(TagFromName.RTPlanTime, "RTPlanTime");
        ensureInAttributeMetadata(TagFromName.RTReferencedSeriesSequence, "RTReferencedSeriesSequence");
        ensureInAttributeMetadata(TagFromName.RTReferencedStudySequence, "RTReferencedStudySequence");
        ensureInAttributeMetadata(TagFromName.RTRelatedROISequence, "RTRelatedROISequence");
        ensureInAttributeMetadata(TagFromName.RTROIIdentificationCodeSequence, "RTROIIdentificationCodeSequence");
        ensureInAttributeMetadata(TagFromName.RTROIInterpretedType, "RTROIInterpretedType");
        ensureInAttributeMetadata(TagFromName.RTROIObservationsSequence, "RTROIObservationsSequence");
        ensureInAttributeMetadata(TagFromName.RTROIRelationship, "RTROIRelationship");
        ensureInAttributeMetadata(TagFromName.RunLengthTriplet, "RunLengthTriplet");
        ensureInAttributeMetadata(TagFromName.RWavePointer, "RWavePointer");
        ensureInAttributeMetadata(TagFromName.RWaveTimeVector, "RWaveTimeVector");
        ensureInAttributeMetadata(TagFromName.SafePositionExitDate, "SafePositionExitDate");
        ensureInAttributeMetadata(TagFromName.SafePositionExitTime, "SafePositionExitTime");
        ensureInAttributeMetadata(TagFromName.SafePositionReturnDate, "SafePositionReturnDate");
        ensureInAttributeMetadata(TagFromName.SafePositionReturnTime, "SafePositionReturnTime");
        ensureInAttributeMetadata(TagFromName.SampleRate, "SampleRate");
        ensureInAttributeMetadata(TagFromName.SamplesPerPixel, "SamplesPerPixel");
        ensureInAttributeMetadata(TagFromName.SamplesPerPixelUsed, "SamplesPerPixelUsed");
        ensureInAttributeMetadata(TagFromName.SamplingFrequency, "SamplingFrequency");
        ensureInAttributeMetadata(TagFromName.SAR, "SAR");
        ensureInAttributeMetadata(TagFromName.SaturationRecovery, "SaturationRecovery");
        ensureInAttributeMetadata(TagFromName.ScanArc, "ScanArc");
        ensureInAttributeMetadata(TagFromName.ScanLength, "ScanLength");
        ensureInAttributeMetadata(TagFromName.ScanMode, "ScanMode");
        ensureInAttributeMetadata(TagFromName.ScanningSequence, "ScanningSequence");
        ensureInAttributeMetadata(TagFromName.ScanningSpotSize, "ScanningSpotSize");
        ensureInAttributeMetadata(TagFromName.ScanOptions, "ScanOptions");
        ensureInAttributeMetadata(TagFromName.ScanSpotMetersetsDelivered, "ScanSpotMetersetsDelivered");
        ensureInAttributeMetadata(TagFromName.ScanSpotMetersetWeights, "ScanSpotMetersetWeights");
        ensureInAttributeMetadata(TagFromName.ScanSpotPositionMap, "ScanSpotPositionMap");
        ensureInAttributeMetadata(TagFromName.ScanSpotTuneID, "ScanSpotTuneID");
        ensureInAttributeMetadata(TagFromName.ScanVelocity, "ScanVelocity");
        ensureInAttributeMetadata(TagFromName.ScatterCorrectionMethod, "ScatterCorrectionMethod");
        ensureInAttributeMetadata(TagFromName.ScatterFractionFactor, "ScatterFractionFactor");
        ensureInAttributeMetadata(TagFromName.ScheduledAdmissionDate, "ScheduledAdmissionDate");
        ensureInAttributeMetadata(TagFromName.ScheduledAdmissionTime, "ScheduledAdmissionTime");
        ensureInAttributeMetadata(TagFromName.ScheduledDischargeDate, "ScheduledDischargeDate");
        ensureInAttributeMetadata(TagFromName.ScheduledDischargeTime, "ScheduledDischargeTime");
        ensureInAttributeMetadata(TagFromName.ScheduledHumanPerformersSequence, "ScheduledHumanPerformersSequence");
        ensureInAttributeMetadata(TagFromName.ScheduledPatientInstitutionResidence,
                "ScheduledPatientInstitutionResidence");
        ensureInAttributeMetadata(TagFromName.ScheduledPerformingPhysicianIdentificationSequence,
                "ScheduledPerformingPhysicianIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.ScheduledPerformingPhysicianName, "ScheduledPerformingPhysicianName");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepDescription, "ScheduledProcedureStepDescription");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepEndDate, "ScheduledProcedureStepEndDate");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepEndTime, "ScheduledProcedureStepEndTime");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepID, "ScheduledProcedureStepID");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepLocation, "ScheduledProcedureStepLocation");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepModificationDateAndTime,
                "ScheduledProcedureStepModificationDateAndTime");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepSequence, "ScheduledProcedureStepSequence");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepStartDate, "ScheduledProcedureStepStartDate");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepStartDateAndTime,
                "ScheduledProcedureStepStartDateAndTime");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepStartTime, "ScheduledProcedureStepStartTime");
        ensureInAttributeMetadata(TagFromName.ScheduledProcedureStepStatus, "ScheduledProcedureStepStatus");
        ensureInAttributeMetadata(TagFromName.ScheduledProcessingApplicationsCodeSequence,
                "ScheduledProcessingApplicationsCodeSequence");
        ensureInAttributeMetadata(TagFromName.ScheduledProtocolCodeSequence, "ScheduledProtocolCodeSequence");
        ensureInAttributeMetadata(TagFromName.ScheduledStationAETitle, "ScheduledStationAETitle");
        ensureInAttributeMetadata(TagFromName.ScheduledStationClassCodeSequence, "ScheduledStationClassCodeSequence");
        ensureInAttributeMetadata(TagFromName.ScheduledStationGeographicLocationCodeSequence,
                "ScheduledStationGeographicLocationCodeSequence");
        ensureInAttributeMetadata(TagFromName.ScheduledStationName, "ScheduledStationName");
        ensureInAttributeMetadata(TagFromName.ScheduledStationNameCodeSequence, "ScheduledStationNameCodeSequence");
        ensureInAttributeMetadata(TagFromName.ScheduledStepAttributesSequence, "ScheduledStepAttributesSequence");
        ensureInAttributeMetadata(TagFromName.ScheduledStudyLocation, "ScheduledStudyLocation");
        ensureInAttributeMetadata(TagFromName.ScheduledStudyLocationAETitle, "ScheduledStudyLocationAETitle");
        ensureInAttributeMetadata(TagFromName.ScheduledStudyStartDate, "ScheduledStudyStartDate");
        ensureInAttributeMetadata(TagFromName.ScheduledStudyStartTime, "ScheduledStudyStartTime");
        ensureInAttributeMetadata(TagFromName.ScheduledStudyStopDate, "ScheduledStudyStopDate");
        ensureInAttributeMetadata(TagFromName.ScheduledStudyStopTime, "ScheduledStudyStopTime");
        ensureInAttributeMetadata(TagFromName.ScheduledWorkitemCodeSequence, "ScheduledWorkitemCodeSequence");
        ensureInAttributeMetadata(TagFromName.ScreenMinimumColorBitDepth, "ScreenMinimumColorBitDepth");
        ensureInAttributeMetadata(TagFromName.ScreenMinimumGrayscaleBitDepth, "ScreenMinimumGrayscaleBitDepth");
        ensureInAttributeMetadata(TagFromName.SecondaryCaptureDeviceID, "SecondaryCaptureDeviceID");
        ensureInAttributeMetadata(TagFromName.SecondaryCaptureDeviceManufacturer, "SecondaryCaptureDeviceManufacturer");
        ensureInAttributeMetadata(TagFromName.SecondaryCaptureDeviceManufacturerModelName,
                "SecondaryCaptureDeviceManufacturerModelName");
        ensureInAttributeMetadata(TagFromName.SecondaryCaptureDeviceSoftwareVersion,
                "SecondaryCaptureDeviceSoftwareVersion");
        ensureInAttributeMetadata(TagFromName.SecondaryCountsAccumulated, "SecondaryCountsAccumulated");
        ensureInAttributeMetadata(TagFromName.SecondaryCountsType, "SecondaryCountsType");
        ensureInAttributeMetadata(TagFromName.SecondaryPositionerIncrement, "SecondaryPositionerIncrement");
        ensureInAttributeMetadata(TagFromName.SecondaryPositionerScanArc, "SecondaryPositionerScanArc");
        ensureInAttributeMetadata(TagFromName.SecondaryPositionerScanStartAngle, "SecondaryPositionerScanStartAngle");
        ensureInAttributeMetadata(TagFromName.SegmentAlgorithmName, "SegmentAlgorithmName");
        ensureInAttributeMetadata(TagFromName.SegmentAlgorithmType, "SegmentAlgorithmType");
        ensureInAttributeMetadata(TagFromName.SegmentationFractionalType, "SegmentationFractionalType");
        ensureInAttributeMetadata(TagFromName.SegmentationType, "SegmentationType");
        ensureInAttributeMetadata(TagFromName.SegmentDescription, "SegmentDescription");
        ensureInAttributeMetadata(TagFromName.SegmentedBluePaletteColorLookupTableData,
                "SegmentedBluePaletteColorLookupTableData");
        ensureInAttributeMetadata(TagFromName.SegmentedGreenPaletteColorLookupTableData,
                "SegmentedGreenPaletteColorLookupTableData");
        ensureInAttributeMetadata(TagFromName.SegmentedKSpaceTraversal, "SegmentedKSpaceTraversal");
        ensureInAttributeMetadata(TagFromName.SegmentedPropertyCategoryCodeSequence,
                "SegmentedPropertyCategoryCodeSequence");
        ensureInAttributeMetadata(TagFromName.SegmentedPropertyTypeCodeSequence, "SegmentedPropertyTypeCodeSequence");
        ensureInAttributeMetadata(TagFromName.SegmentedRedPaletteColorLookupTableData,
                "SegmentedRedPaletteColorLookupTableData");
        ensureInAttributeMetadata(TagFromName.SegmentIdentificationSequence, "SegmentIdentificationSequence");
        ensureInAttributeMetadata(TagFromName.SegmentLabel, "SegmentLabel");
        ensureInAttributeMetadata(TagFromName.SegmentNumber, "SegmentNumber");
        ensureInAttributeMetadata(TagFromName.SegmentSequence, "SegmentSequence");
        ensureInAttributeMetadata(TagFromName.SelectorAttribute, "SelectorAttribute");
        ensureInAttributeMetadata(TagFromName.SelectorAttributePrivateCreator, "SelectorAttributePrivateCreator");
        ensureInAttributeMetadata(TagFromName.SelectorAttributeVR, "SelectorAttributeVR");
        ensureInAttributeMetadata(TagFromName.SelectorATValue, "SelectorATValue");
        ensureInAttributeMetadata(TagFromName.SelectorCodeSequenceValue, "SelectorCodeSequenceValue");
        ensureInAttributeMetadata(TagFromName.SelectorCSValue, "SelectorCSValue");
        ensureInAttributeMetadata(TagFromName.SelectorDSValue, "SelectorDSValue");
        ensureInAttributeMetadata(TagFromName.SelectorFDValue, "SelectorFDValue");
        ensureInAttributeMetadata(TagFromName.SelectorFLValue, "SelectorFLValue");
        ensureInAttributeMetadata(TagFromName.SelectorISValue, "SelectorISValue");
        ensureInAttributeMetadata(TagFromName.SelectorLOValue, "SelectorLOValue");
        ensureInAttributeMetadata(TagFromName.SelectorLTValue, "SelectorLTValue");
        ensureInAttributeMetadata(TagFromName.SelectorPNValue, "SelectorPNValue");
        ensureInAttributeMetadata(TagFromName.SelectorSequencePointer, "SelectorSequencePointer");
        ensureInAttributeMetadata(TagFromName.SelectorSequencePointerPrivateCreator,
                "SelectorSequencePointerPrivateCreator");
        ensureInAttributeMetadata(TagFromName.SelectorSHValue, "SelectorSHValue");
        ensureInAttributeMetadata(TagFromName.SelectorSLValue, "SelectorSLValue");
        ensureInAttributeMetadata(TagFromName.SelectorSSValue, "SelectorSSValue");
        ensureInAttributeMetadata(TagFromName.SelectorSTValue, "SelectorSTValue");
        ensureInAttributeMetadata(TagFromName.SelectorULValue, "SelectorULValue");
        ensureInAttributeMetadata(TagFromName.SelectorUSValue, "SelectorUSValue");
        ensureInAttributeMetadata(TagFromName.SelectorUTValue, "SelectorUTValue");
        ensureInAttributeMetadata(TagFromName.SelectorValueNumber, "SelectorValueNumber");
        ensureInAttributeMetadata(TagFromName.Sensitivity, "Sensitivity");
        ensureInAttributeMetadata(TagFromName.SequenceDelimitationItem, "SequenceDelimitationItem");
        ensureInAttributeMetadata(TagFromName.SequenceName, "SequenceName");
        ensureInAttributeMetadata(TagFromName.SequenceOfCompressedData, "SequenceOfCompressedData");
        ensureInAttributeMetadata(TagFromName.SequenceOfUltrasoundRegions, "SequenceOfUltrasoundRegions");
        ensureInAttributeMetadata(TagFromName.SequenceVariant, "SequenceVariant");
        // ensureInAttributeMetadata(TagFromName.SequencingIndicatorTrial,
        // "SequencingIndicatorTrial");
        ensureInAttributeMetadata(TagFromName.SeriesDate, "SeriesDate");
        ensureInAttributeMetadata(TagFromName.SeriesDescription, "SeriesDescription");
        ensureInAttributeMetadata(TagFromName.SeriesInstanceUID, "SeriesInstanceUID");
        ensureInAttributeMetadata(TagFromName.SeriesInStudy, "SeriesInStudy");
        ensureInAttributeMetadata(TagFromName.SeriesNumber, "SeriesNumber");
        ensureInAttributeMetadata(TagFromName.SeriesTime, "SeriesTime");
        ensureInAttributeMetadata(TagFromName.SeriesType, "SeriesType");
        ensureInAttributeMetadata(TagFromName.ServiceEpisodeDescription, "ServiceEpisodeDescription");
        ensureInAttributeMetadata(TagFromName.ServiceEpisodeID, "ServiceEpisodeID");
        ensureInAttributeMetadata(TagFromName.SetupDeviceDescription, "SetupDeviceDescription");
        ensureInAttributeMetadata(TagFromName.SetupDeviceLabel, "SetupDeviceLabel");
        ensureInAttributeMetadata(TagFromName.SetupDeviceParameter, "SetupDeviceParameter");
        ensureInAttributeMetadata(TagFromName.SetupDeviceSequence, "SetupDeviceSequence");
        ensureInAttributeMetadata(TagFromName.SetupDeviceType, "SetupDeviceType");
        ensureInAttributeMetadata(TagFromName.SetupImageComment, "SetupImageComment");
        ensureInAttributeMetadata(TagFromName.SetupReferenceDescription, "SetupReferenceDescription");
        ensureInAttributeMetadata(TagFromName.SetupTechnique, "SetupTechnique");
        ensureInAttributeMetadata(TagFromName.SetupTechniqueDescription, "SetupTechniqueDescription");
        ensureInAttributeMetadata(TagFromName.ShapeType, "ShapeType");
        ensureInAttributeMetadata(TagFromName.SharedFunctionalGroupsSequence, "SharedFunctionalGroupsSequence");
        ensureInAttributeMetadata(TagFromName.ShieldingDeviceDescription, "ShieldingDeviceDescription");
        ensureInAttributeMetadata(TagFromName.ShieldingDeviceLabel, "ShieldingDeviceLabel");
        ensureInAttributeMetadata(TagFromName.ShieldingDevicePosition, "ShieldingDevicePosition");
        ensureInAttributeMetadata(TagFromName.ShieldingDeviceSequence, "ShieldingDeviceSequence");
        ensureInAttributeMetadata(TagFromName.ShieldingDeviceType, "ShieldingDeviceType");
        ensureInAttributeMetadata(TagFromName.ShiftTableSize, "ShiftTableSize");
        ensureInAttributeMetadata(TagFromName.ShiftTableTriplet, "ShiftTableTriplet");
        ensureInAttributeMetadata(TagFromName.ShowAcquisitionTechniquesFlag, "ShowAcquisitionTechniquesFlag");
        ensureInAttributeMetadata(TagFromName.ShowGraphicAnnotationFlag, "ShowGraphicAnnotationFlag");
        ensureInAttributeMetadata(TagFromName.ShowGrayscaleInverted, "ShowGrayscaleInverted");
        ensureInAttributeMetadata(TagFromName.ShowImageTrueSizeFlag, "ShowImageTrueSizeFlag");
        ensureInAttributeMetadata(TagFromName.ShowPatientDemographicsFlag, "ShowPatientDemographicsFlag");
        ensureInAttributeMetadata(TagFromName.ShutterLeftVerticalEdge, "ShutterLeftVerticalEdge");
        ensureInAttributeMetadata(TagFromName.ShutterLowerHorizontalEdge, "ShutterLowerHorizontalEdge");
        ensureInAttributeMetadata(TagFromName.ShutterOverlayGroup, "ShutterOverlayGroup");
        ensureInAttributeMetadata(TagFromName.ShutterPresentationColorCIELabValue,
                "ShutterPresentationColorCIELabValue");
        ensureInAttributeMetadata(TagFromName.ShutterPresentationValue, "ShutterPresentationValue");
        ensureInAttributeMetadata(TagFromName.ShutterRightVerticalEdge, "ShutterRightVerticalEdge");
        ensureInAttributeMetadata(TagFromName.ShutterShape, "ShutterShape");
        ensureInAttributeMetadata(TagFromName.ShutterUpperHorizontalEdge, "ShutterUpperHorizontalEdge");
        ensureInAttributeMetadata(TagFromName.SignalDomainColumns, "SignalDomainColumns");
        ensureInAttributeMetadata(TagFromName.SignalDomainRows, "SignalDomainRows");
        ensureInAttributeMetadata(TagFromName.Signature, "Signature");
        ensureInAttributeMetadata(TagFromName.SingleCollimationWidth, "SingleCollimationWidth");
        ensureInAttributeMetadata(TagFromName.SkipBeats, "SkipBeats");
        ensureInAttributeMetadata(TagFromName.SkipFrameRangeFlag, "SkipFrameRangeFlag");
        ensureInAttributeMetadata(TagFromName.SlabOrientation, "SlabOrientation");
        ensureInAttributeMetadata(TagFromName.SlabThickness, "SlabThickness");
        ensureInAttributeMetadata(TagFromName.SliceLocation, "SliceLocation");
        ensureInAttributeMetadata(TagFromName.SliceLocationVector, "SliceLocationVector");
        ensureInAttributeMetadata(TagFromName.SliceProgressionDirection, "SliceProgressionDirection");
        ensureInAttributeMetadata(TagFromName.SliceSensitivityFactor, "SliceSensitivityFactor");
        ensureInAttributeMetadata(TagFromName.SliceThickness, "SliceThickness");
        ensureInAttributeMetadata(TagFromName.SliceVector, "SliceVector");
        ensureInAttributeMetadata(TagFromName.SlideIdentifier, "SlideIdentifier");
        ensureInAttributeMetadata(TagFromName.SmallestImagePixelValue, "SmallestImagePixelValue");
        ensureInAttributeMetadata(TagFromName.SmallestPixelValueInPlane, "SmallestPixelValueInPlane");
        ensureInAttributeMetadata(TagFromName.SmallestPixelValueInSeries, "SmallestPixelValueInSeries");
        ensureInAttributeMetadata(TagFromName.SmallestValidPixelValue, "SmallestValidPixelValue");
        ensureInAttributeMetadata(TagFromName.SmokingStatus, "SmokingStatus");
        ensureInAttributeMetadata(TagFromName.SmoothingType, "SmoothingType");
        ensureInAttributeMetadata(TagFromName.SnoutID, "SnoutID");
        ensureInAttributeMetadata(TagFromName.SnoutPosition, "SnoutPosition");
        ensureInAttributeMetadata(TagFromName.SnoutPositionTolerance, "SnoutPositionTolerance");
        ensureInAttributeMetadata(TagFromName.SnoutSequence, "SnoutSequence");
        ensureInAttributeMetadata(TagFromName.SoftcopyVOILUTSequence, "SoftcopyVOILUTSequence");
        ensureInAttributeMetadata(TagFromName.SoftTissueFocusThermalIndex, "SoftTissueFocusThermalIndex");
        ensureInAttributeMetadata(TagFromName.SoftTissueSurfaceThermalIndex, "SoftTissueSurfaceThermalIndex");
        ensureInAttributeMetadata(TagFromName.SoftTissueThermalIndex, "SoftTissueThermalIndex");
        ensureInAttributeMetadata(TagFromName.SoftwareVersion, "SoftwareVersion");
        ensureInAttributeMetadata(TagFromName.SOPAuthorizationComment, "SOPAuthorizationComment");
        ensureInAttributeMetadata(TagFromName.SOPAuthorizationDateAndTime, "SOPAuthorizationDateAndTime");
        ensureInAttributeMetadata(TagFromName.SOPClassesInStudy, "SOPClassesInStudy");
        ensureInAttributeMetadata(TagFromName.SOPClassesSupported, "SOPClassesSupported");
        ensureInAttributeMetadata(TagFromName.SOPClassUID, "SOPClassUID");
        ensureInAttributeMetadata(TagFromName.SOPInstanceStatus, "SOPInstanceStatus");
        ensureInAttributeMetadata(TagFromName.SOPInstanceUID, "SOPInstanceUID");
        ensureInAttributeMetadata(TagFromName.SortByCategory, "SortByCategory");
        ensureInAttributeMetadata(TagFromName.SortingDirection, "SortingDirection");
        ensureInAttributeMetadata(TagFromName.SortingOperationsSequence, "SortingOperationsSequence");
        ensureInAttributeMetadata(TagFromName.SourceApplicationEntityTitle, "SourceApplicationEntityTitle");
        ensureInAttributeMetadata(TagFromName.SourceApplicatorID, "SourceApplicatorID");
        ensureInAttributeMetadata(TagFromName.SourceApplicatorLength, "SourceApplicatorLength");
        ensureInAttributeMetadata(TagFromName.SourceApplicatorManufacturer, "SourceApplicatorManufacturer");
        ensureInAttributeMetadata(TagFromName.SourceApplicatorName, "SourceApplicatorName");
        ensureInAttributeMetadata(TagFromName.SourceApplicatorNumber, "SourceApplicatorNumber");
        ensureInAttributeMetadata(TagFromName.SourceApplicatorStepSize, "SourceApplicatorStepSize");
        ensureInAttributeMetadata(TagFromName.SourceApplicatorType, "SourceApplicatorType");
        ensureInAttributeMetadata(TagFromName.SourceApplicatorWallNominalThickness,
                "SourceApplicatorWallNominalThickness");
        ensureInAttributeMetadata(TagFromName.SourceApplicatorWallNominalTransmission,
                "SourceApplicatorWallNominalTransmission");
        ensureInAttributeMetadata(TagFromName.SourceAxisDistance, "SourceAxisDistance");
        ensureInAttributeMetadata(TagFromName.SourceEncapsulationNominalThickness,
                "SourceEncapsulationNominalThickness");
        ensureInAttributeMetadata(TagFromName.SourceEncapsulationNominalTransmission,
                "SourceEncapsulationNominalTransmission");
        ensureInAttributeMetadata(TagFromName.SourceFrameOfReferenceUID, "SourceFrameOfReferenceUID");
        ensureInAttributeMetadata(TagFromName.SourceHangingProtocolSequence, "SourceHangingProtocolSequence");
        ensureInAttributeMetadata(TagFromName.SourceImageEvidenceSequence, "SourceImageEvidenceSequence");
        ensureInAttributeMetadata(TagFromName.SourceImageID, "SourceImageID");
        ensureInAttributeMetadata(TagFromName.SourceImageSequence, "SourceImageSequence");
        ensureInAttributeMetadata(TagFromName.SourceInstanceSequence, "SourceInstanceSequence");
        ensureInAttributeMetadata(TagFromName.SourceIsotopeHalfLife, "SourceIsotopeHalfLife");
        ensureInAttributeMetadata(TagFromName.SourceIsotopeName, "SourceIsotopeName");
        ensureInAttributeMetadata(TagFromName.SourceManufacturer, "SourceManufacturer");
        ensureInAttributeMetadata(TagFromName.SourceMovementType, "SourceMovementType");
        ensureInAttributeMetadata(TagFromName.SourceNumber, "SourceNumber");
        ensureInAttributeMetadata(TagFromName.SourceOfPreviousValues, "SourceOfPreviousValues");
        ensureInAttributeMetadata(TagFromName.SourceSequence, "SourceSequence");
        ensureInAttributeMetadata(TagFromName.SourceSerialNumber, "SourceSerialNumber");
        ensureInAttributeMetadata(TagFromName.SourceStrength, "SourceStrength");
        ensureInAttributeMetadata(TagFromName.SourceStrengthReferenceDate, "SourceStrengthReferenceDate");
        ensureInAttributeMetadata(TagFromName.SourceStrengthReferenceTime, "SourceStrengthReferenceTime");
        ensureInAttributeMetadata(TagFromName.SourceStrengthUnits, "SourceStrengthUnits");
        ensureInAttributeMetadata(TagFromName.SourceToBeamLimitingDeviceDistance, "SourceToBeamLimitingDeviceDistance");
        ensureInAttributeMetadata(TagFromName.SourceToBlockTrayDistance, "SourceToBlockTrayDistance");
        ensureInAttributeMetadata(TagFromName.SourceToCompensatorDistance, "SourceToCompensatorDistance");
        ensureInAttributeMetadata(TagFromName.SourceToCompensatorTrayDistance, "SourceToCompensatorTrayDistance");
        ensureInAttributeMetadata(TagFromName.SourceToReferenceObjectDistance, "SourceToReferenceObjectDistance");
        ensureInAttributeMetadata(TagFromName.SourceToSurfaceDistance, "SourceToSurfaceDistance");
        ensureInAttributeMetadata(TagFromName.SourceToWedgeTrayDistance, "SourceToWedgeTrayDistance");
        ensureInAttributeMetadata(TagFromName.SourceType, "SourceType");
        ensureInAttributeMetadata(TagFromName.SourceWaveformSequence, "SourceWaveformSequence");
        ensureInAttributeMetadata(TagFromName.SpacingBetweenSlices, "SpacingBetweenSlices");
        ensureInAttributeMetadata(TagFromName.SpatialLocationsPreserved, "SpatialLocationsPreserved");
        ensureInAttributeMetadata(TagFromName.SpatialPresaturation, "SpatialPresaturation");
        ensureInAttributeMetadata(TagFromName.SpatialResolution, "SpatialResolution");
        ensureInAttributeMetadata(TagFromName.SpecialNeeds, "SpecialNeeds");
        ensureInAttributeMetadata(TagFromName.SpecificAbsorptionRateDefinition, "SpecificAbsorptionRateDefinition");
        ensureInAttributeMetadata(TagFromName.SpecificAbsorptionRateSequence, "SpecificAbsorptionRateSequence");
        ensureInAttributeMetadata(TagFromName.SpecificAbsorptionRateValue, "SpecificAbsorptionRateValue");
        ensureInAttributeMetadata(TagFromName.SpecificCharacterSet, "SpecificCharacterSet");
        ensureInAttributeMetadata(TagFromName.SpecifiedChannelTotalTime, "SpecifiedChannelTotalTime");
        ensureInAttributeMetadata(TagFromName.SpecifiedMeterset, "SpecifiedMeterset");
        ensureInAttributeMetadata(TagFromName.SpecifiedNumberOfPulses, "SpecifiedNumberOfPulses");
        ensureInAttributeMetadata(TagFromName.SpecifiedPrimaryMeterset, "SpecifiedPrimaryMeterset");
        ensureInAttributeMetadata(TagFromName.SpecifiedPulseRepetitionInterval, "SpecifiedPulseRepetitionInterval");
        ensureInAttributeMetadata(TagFromName.SpecifiedSecondaryMeterset, "SpecifiedSecondaryMeterset");
        ensureInAttributeMetadata(TagFromName.SpecifiedTreatmentTime, "SpecifiedTreatmentTime");
        ensureInAttributeMetadata(TagFromName.SpecimenAccessionNumber, "SpecimenAccessionNumber");
        ensureInAttributeMetadata(TagFromName.SpecimenDescriptionSequenceTrial, "SpecimenDescriptionSequenceTrial");
        ensureInAttributeMetadata(TagFromName.SpecimenDescriptionTrial, "SpecimenDescriptionTrial");
        ensureInAttributeMetadata(TagFromName.SpecimenIdentifier, "SpecimenIdentifier");
        ensureInAttributeMetadata(TagFromName.SpecimenSequence, "SpecimenSequence");
        ensureInAttributeMetadata(TagFromName.SpecimenTypeCodeSequence, "SpecimenTypeCodeSequence");
        ensureInAttributeMetadata(TagFromName.SpectrallySelectedExcitation, "SpectrallySelectedExcitation");
        ensureInAttributeMetadata(TagFromName.SpectrallySelectedSuppression, "SpectrallySelectedSuppression");
        ensureInAttributeMetadata(TagFromName.SpectralWidth, "SpectralWidth");
        ensureInAttributeMetadata(TagFromName.SpectroscopyAcquisitionDataColumns, "SpectroscopyAcquisitionDataColumns");
        ensureInAttributeMetadata(TagFromName.SpectroscopyAcquisitionOutOfPlanePhaseSteps,
                "SpectroscopyAcquisitionOutOfPlanePhaseSteps");
        ensureInAttributeMetadata(TagFromName.SpectroscopyAcquisitionPhaseColumns,
                "SpectroscopyAcquisitionPhaseColumns");
        ensureInAttributeMetadata(TagFromName.SpectroscopyAcquisitionPhaseRows, "SpectroscopyAcquisitionPhaseRows");
        ensureInAttributeMetadata(TagFromName.SpectroscopyData, "SpectroscopyData");
        ensureInAttributeMetadata(TagFromName.SphericalLensPower, "SphericalLensPower");
        ensureInAttributeMetadata(TagFromName.SpiralPitchFactor, "SpiralPitchFactor");
        ensureInAttributeMetadata(TagFromName.Spoiling, "Spoiling");
        ensureInAttributeMetadata(TagFromName.StackID, "StackID");
        ensureInAttributeMetadata(TagFromName.StageCodeSequence, "StageCodeSequence");
        ensureInAttributeMetadata(TagFromName.StageName, "StageName");
        ensureInAttributeMetadata(TagFromName.StageNumber, "StageNumber");
        ensureInAttributeMetadata(TagFromName.StartAcquisitionDateTime, "StartAcquisitionDateTime");
        ensureInAttributeMetadata(TagFromName.StartAngle, "StartAngle");
        ensureInAttributeMetadata(TagFromName.StartCumulativeMetersetWeight, "StartCumulativeMetersetWeight");
        ensureInAttributeMetadata(TagFromName.StartingRespiratoryAmplitude, "StartingRespiratoryAmplitude");
        ensureInAttributeMetadata(TagFromName.StartingRespiratoryPhase, "StartingRespiratoryPhase");
        ensureInAttributeMetadata(TagFromName.StartMeterset, "StartMeterset");
        ensureInAttributeMetadata(TagFromName.StartTrim, "StartTrim");
        ensureInAttributeMetadata(TagFromName.StationName, "StationName");
        ensureInAttributeMetadata(TagFromName.Status, "Status");
        ensureInAttributeMetadata(TagFromName.SteadyStatePulseSequence, "SteadyStatePulseSequence");
        ensureInAttributeMetadata(TagFromName.SteeringAngle, "SteeringAngle");
        ensureInAttributeMetadata(TagFromName.StereoBaselineAngle, "StereoBaselineAngle");
        ensureInAttributeMetadata(TagFromName.StereoBaselineDisplacement, "StereoBaselineDisplacement");
        ensureInAttributeMetadata(TagFromName.StereoHorizontalPixelOffset, "StereoHorizontalPixelOffset");
        ensureInAttributeMetadata(TagFromName.StereoPairsSequence, "StereoPairsSequence");
        ensureInAttributeMetadata(TagFromName.StereoRotation, "StereoRotation");
        ensureInAttributeMetadata(TagFromName.StereoVerticalPixelOffset, "StereoVerticalPixelOffset");
        ensureInAttributeMetadata(TagFromName.StopTrim, "StopTrim");
        ensureInAttributeMetadata(TagFromName.StorageMediaFileSetID, "StorageMediaFileSetID");
        ensureInAttributeMetadata(TagFromName.StorageMediaFileSetUID, "StorageMediaFileSetUID");
        ensureInAttributeMetadata(TagFromName.StructureSetDate, "StructureSetDate");
        ensureInAttributeMetadata(TagFromName.StructureSetDescription, "StructureSetDescription");
        ensureInAttributeMetadata(TagFromName.StructureSetLabel, "StructureSetLabel");
        ensureInAttributeMetadata(TagFromName.StructureSetName, "StructureSetName");
        ensureInAttributeMetadata(TagFromName.StructureSetROISequence, "StructureSetROISequence");
        ensureInAttributeMetadata(TagFromName.StructureSetTime, "StructureSetTime");
        ensureInAttributeMetadata(TagFromName.StudiesContainingOtherReferencedInstancesSequence,
                "StudiesContainingOtherReferencedInstancesSequence");
        ensureInAttributeMetadata(TagFromName.StudyArrivalDate, "StudyArrivalDate");
        ensureInAttributeMetadata(TagFromName.StudyArrivalTime, "StudyArrivalTime");
        ensureInAttributeMetadata(TagFromName.StudyComments, "StudyComments");
        ensureInAttributeMetadata(TagFromName.StudyCompletionDate, "StudyCompletionDate");
        ensureInAttributeMetadata(TagFromName.StudyCompletionTime, "StudyCompletionTime");
        ensureInAttributeMetadata(TagFromName.StudyComponentStatusID, "StudyComponentStatusID");
        ensureInAttributeMetadata(TagFromName.StudyDate, "StudyDate");
        ensureInAttributeMetadata(TagFromName.StudyDescription, "StudyDescription");
        ensureInAttributeMetadata(TagFromName.StudyID, "StudyID");
        ensureInAttributeMetadata(TagFromName.StudyIDIssuer, "StudyIDIssuer");
        ensureInAttributeMetadata(TagFromName.StudyInstanceUID, "StudyInstanceUID");
        ensureInAttributeMetadata(TagFromName.StudyPriorityID, "StudyPriorityID");
        ensureInAttributeMetadata(TagFromName.StudyReadDate, "StudyReadDate");
        ensureInAttributeMetadata(TagFromName.StudyReadTime, "StudyReadTime");
        ensureInAttributeMetadata(TagFromName.StudyStatusID, "StudyStatusID");
        ensureInAttributeMetadata(TagFromName.StudyTime, "StudyTime");
        ensureInAttributeMetadata(TagFromName.StudyVerifiedDate, "StudyVerifiedDate");
        ensureInAttributeMetadata(TagFromName.StudyVerifiedTime, "StudyVerifiedTime");
        ensureInAttributeMetadata(TagFromName.SubstanceAdministrationApproval, "SubstanceAdministrationApproval");
        ensureInAttributeMetadata(TagFromName.SubstanceAdministrationDateTime, "SubstanceAdministrationDateTime");
        ensureInAttributeMetadata(TagFromName.SubstanceAdministrationDeviceID, "SubstanceAdministrationDeviceID");
        ensureInAttributeMetadata(TagFromName.SubstanceAdministrationNotes, "SubstanceAdministrationNotes");
        ensureInAttributeMetadata(TagFromName.SubstanceAdministrationParameterSequence,
                "SubstanceAdministrationParameterSequence");
        ensureInAttributeMetadata(TagFromName.SubtractionItemID, "SubtractionItemID");
        ensureInAttributeMetadata(TagFromName.SupportedImageDisplayFormatsSequence,
                "SupportedImageDisplayFormatsSequence");
        ensureInAttributeMetadata(TagFromName.SurfaceEntryPoint, "SurfaceEntryPoint");
        ensureInAttributeMetadata(TagFromName.SynchronizationChannel, "SynchronizationChannel");
        ensureInAttributeMetadata(TagFromName.SynchronizationFrameOfReferenceUID, "SynchronizationFrameOfReferenceUID");
        ensureInAttributeMetadata(TagFromName.SynchronizationTrigger, "SynchronizationTrigger");
        ensureInAttributeMetadata(TagFromName.SynchronizedScrollingSequence, "SynchronizedScrollingSequence");
        ensureInAttributeMetadata(TagFromName.SyringeCounts, "SyringeCounts");
        ensureInAttributeMetadata(TagFromName.T2Preparation, "T2Preparation");
        ensureInAttributeMetadata(TagFromName.TableAngle, "TableAngle");
        ensureInAttributeMetadata(TagFromName.TableCradleTiltAngle, "TableCradleTiltAngle");
        ensureInAttributeMetadata(TagFromName.TableFeedPerRotation, "TableFeedPerRotation");
        ensureInAttributeMetadata(TagFromName.TableHeadTiltAngle, "TableHeadTiltAngle");
        ensureInAttributeMetadata(TagFromName.TableHeight, "TableHeight");
        ensureInAttributeMetadata(TagFromName.TableHorizontalRotationAngle, "TableHorizontalRotationAngle");
        ensureInAttributeMetadata(TagFromName.TableLateralIncrement, "TableLateralIncrement");
        ensureInAttributeMetadata(TagFromName.TableLongitudinalIncrement, "TableLongitudinalIncrement");
        ensureInAttributeMetadata(TagFromName.TableMotion, "TableMotion");
        ensureInAttributeMetadata(TagFromName.TableOfParameterValues, "TableOfParameterValues");
        ensureInAttributeMetadata(TagFromName.TableOfPixelValues, "TableOfPixelValues");
        ensureInAttributeMetadata(TagFromName.TableOfXBreakPoints, "TableOfXBreakPoints");
        ensureInAttributeMetadata(TagFromName.TableOfYBreakPoints, "TableOfYBreakPoints");
        ensureInAttributeMetadata(TagFromName.TablePosition, "TablePosition");
        ensureInAttributeMetadata(TagFromName.TablePositionSequence, "TablePositionSequence");
        ensureInAttributeMetadata(TagFromName.TableSpeed, "TableSpeed");
        ensureInAttributeMetadata(TagFromName.TableTopEccentricAngle, "TableTopEccentricAngle");
        ensureInAttributeMetadata(TagFromName.TableTopEccentricAngleTolerance, "TableTopEccentricAngleTolerance");
        ensureInAttributeMetadata(TagFromName.TableTopEccentricAxisDistance, "TableTopEccentricAxisDistance");
        ensureInAttributeMetadata(TagFromName.TableTopEccentricRotationDirection, "TableTopEccentricRotationDirection");
        ensureInAttributeMetadata(TagFromName.TableTopLateralPosition, "TableTopLateralPosition");
        ensureInAttributeMetadata(TagFromName.TableTopLateralPositionTolerance, "TableTopLateralPositionTolerance");
        ensureInAttributeMetadata(TagFromName.TableTopLateralSetupDisplacement, "TableTopLateralSetupDisplacement");
        ensureInAttributeMetadata(TagFromName.TableTopLongitudinalPosition, "TableTopLongitudinalPosition");
        ensureInAttributeMetadata(TagFromName.TableTopLongitudinalPositionTolerance,
                "TableTopLongitudinalPositionTolerance");
        ensureInAttributeMetadata(TagFromName.TableTopLongitudinalSetupDisplacement,
                "TableTopLongitudinalSetupDisplacement");
        ensureInAttributeMetadata(TagFromName.TableTopPitchAngle, "TableTopPitchAngle");
        ensureInAttributeMetadata(TagFromName.TableTopPitchAngleTolerance, "TableTopPitchAngleTolerance");
        ensureInAttributeMetadata(TagFromName.TableTopPitchRotationDirection, "TableTopPitchRotationDirection");
        ensureInAttributeMetadata(TagFromName.TableTopRollAngle, "TableTopRollAngle");
        ensureInAttributeMetadata(TagFromName.TableTopRollAngleTolerance, "TableTopRollAngleTolerance");
        ensureInAttributeMetadata(TagFromName.TableTopRollRotationDirection, "TableTopRollRotationDirection");
        ensureInAttributeMetadata(TagFromName.TableTopVerticalPosition, "TableTopVerticalPosition");
        ensureInAttributeMetadata(TagFromName.TableTopVerticalPositionTolerance, "TableTopVerticalPositionTolerance");
        ensureInAttributeMetadata(TagFromName.TableTopVerticalSetupDisplacement, "TableTopVerticalSetupDisplacement");
        ensureInAttributeMetadata(TagFromName.TableTraverse, "TableTraverse");
        ensureInAttributeMetadata(TagFromName.TableType, "TableType");
        ensureInAttributeMetadata(TagFromName.TableVerticalIncrement, "TableVerticalIncrement");
        ensureInAttributeMetadata(TagFromName.TableXPositionToIsocenter, "TableXPositionToIsocenter");
        ensureInAttributeMetadata(TagFromName.TableYPositionToIsocenter, "TableYPositionToIsocenter");
        ensureInAttributeMetadata(TagFromName.TableZPositionToIsocenter, "TableZPositionToIsocenter");
        ensureInAttributeMetadata(TagFromName.TagAngleFirstAxis, "TagAngleFirstAxis");
        ensureInAttributeMetadata(TagFromName.TagAngleSecondAxis, "TagAngleSecondAxis");
        ensureInAttributeMetadata(TagFromName.Tagging, "Tagging");
        ensureInAttributeMetadata(TagFromName.TaggingDelay, "TaggingDelay");
        ensureInAttributeMetadata(TagFromName.TagSpacingFirstDimension, "TagSpacingFirstDimension");
        ensureInAttributeMetadata(TagFromName.TagSpacingSecondDimension, "TagSpacingSecondDimension");
        ensureInAttributeMetadata(TagFromName.TagThickness, "TagThickness");
        ensureInAttributeMetadata(TagFromName.TargetMaximumDose, "TargetMaximumDose");
        ensureInAttributeMetadata(TagFromName.TargetMinimumDose, "TargetMinimumDose");
        ensureInAttributeMetadata(TagFromName.TargetPrescriptionDose, "TargetPrescriptionDose");
        ensureInAttributeMetadata(TagFromName.TargetUnderdoseVolumeFraction, "TargetUnderdoseVolumeFraction");
        ensureInAttributeMetadata(TagFromName.TelephoneNumberTrial, "TelephoneNumberTrial");
        ensureInAttributeMetadata(TagFromName.TemplateExtensionCreatorUID, "TemplateExtensionCreatorUID");
        ensureInAttributeMetadata(TagFromName.TemplateExtensionFlag, "TemplateExtensionFlag");
        ensureInAttributeMetadata(TagFromName.TemplateExtensionOrganizationUID, "TemplateExtensionOrganizationUID");
        ensureInAttributeMetadata(TagFromName.TemplateIdentifier, "TemplateIdentifier");
        ensureInAttributeMetadata(TagFromName.TemplateLocalVersion, "TemplateLocalVersion");
        ensureInAttributeMetadata(TagFromName.TemplateName, "TemplateName");
        ensureInAttributeMetadata(TagFromName.TemplateNumber, "TemplateNumber");
        ensureInAttributeMetadata(TagFromName.TemplateType, "TemplateType");
        ensureInAttributeMetadata(TagFromName.TemplateVersion, "TemplateVersion");
        ensureInAttributeMetadata(TagFromName.TemporalPositionIdentifier, "TemporalPositionIdentifier");
        ensureInAttributeMetadata(TagFromName.TemporalPositionIndex, "TemporalPositionIndex");
        ensureInAttributeMetadata(TagFromName.TemporalRangeType, "TemporalRangeType");
        ensureInAttributeMetadata(TagFromName.TemporalResolution, "TemporalResolution");
        ensureInAttributeMetadata(TagFromName.TerminalType, "TerminalType");
        ensureInAttributeMetadata(TagFromName.TextComments, "TextComments");
        ensureInAttributeMetadata(TagFromName.TextFormatID, "TextFormatID");
        ensureInAttributeMetadata(TagFromName.TextObjectSequence, "TextObjectSequence");
        ensureInAttributeMetadata(TagFromName.TextString, "TextString");
        ensureInAttributeMetadata(TagFromName.TextValue, "TextValue");
        ensureInAttributeMetadata(TagFromName.TherapyDescription, "TherapyDescription");
        ensureInAttributeMetadata(TagFromName.TherapyType, "TherapyType");
        ensureInAttributeMetadata(TagFromName.ThreeDRenderingType, "ThreeDRenderingType");
        ensureInAttributeMetadata(TagFromName.ThresholdDensity, "ThresholdDensity");
        ensureInAttributeMetadata(TagFromName.TIDOffset, "TIDOffset");
        ensureInAttributeMetadata(TagFromName.Time, "Time");
        ensureInAttributeMetadata(TagFromName.TimeBasedImageSetsSequence, "TimeBasedImageSetsSequence");
        ensureInAttributeMetadata(TagFromName.TimeDistributionProtocol, "TimeDistributionProtocol");
        ensureInAttributeMetadata(TagFromName.TimeDomainFiltering, "TimeDomainFiltering");
        // ensureInAttributeMetadata(TagFromName.TimeOfDocumentCreationOrVerbalTransactionTrial,
        // "TimeOfDocumentCreationOrVerbalTransactionTrial");
        ensureInAttributeMetadata(TagFromName.TimeOfFlightContrast, "TimeOfFlightContrast");
        ensureInAttributeMetadata(TagFromName.TimeOfLastCalibration, "TimeOfLastCalibration");
        ensureInAttributeMetadata(TagFromName.TimeOfLastDetectorCalibration, "TimeOfLastDetectorCalibration");
        ensureInAttributeMetadata(TagFromName.TimeOfSecondaryCapture, "TimeOfSecondaryCapture");
        ensureInAttributeMetadata(TagFromName.TimeSliceVector, "TimeSliceVector");
        ensureInAttributeMetadata(TagFromName.TimeSlotInformationSequence, "TimeSlotInformationSequence");
        ensureInAttributeMetadata(TagFromName.TimeSlotNumber, "TimeSlotNumber");
        ensureInAttributeMetadata(TagFromName.TimeSlotTime, "TimeSlotTime");
        ensureInAttributeMetadata(TagFromName.TimeSlotVector, "TimeSlotVector");
        ensureInAttributeMetadata(TagFromName.TimeSource, "TimeSource");
        ensureInAttributeMetadata(TagFromName.TimezoneOffsetFromUTC, "TimezoneOffsetFromUTC");
        ensureInAttributeMetadata(TagFromName.TissueHeterogeneityCorrection, "TissueHeterogeneityCorrection");
        ensureInAttributeMetadata(TagFromName.TMLinePositionX0, "TMLinePositionX0");
        ensureInAttributeMetadata(TagFromName.TMLinePositionX0Retired, "TMLinePositionX0Retired");
        ensureInAttributeMetadata(TagFromName.TMLinePositionX1, "TMLinePositionX1");
        ensureInAttributeMetadata(TagFromName.TMLinePositionX1Retired, "TMLinePositionX1Retired");
        ensureInAttributeMetadata(TagFromName.TMLinePositionY0, "TMLinePositionY0");
        ensureInAttributeMetadata(TagFromName.TMLinePositionY0Retired, "TMLinePositionY0Retired");
        ensureInAttributeMetadata(TagFromName.TMLinePositionY1, "TMLinePositionY1");
        ensureInAttributeMetadata(TagFromName.TMLinePositionY1Retired, "TMLinePositionY1Retired");
        ensureInAttributeMetadata(TagFromName.ToleranceTableLabel, "ToleranceTableLabel");
        ensureInAttributeMetadata(TagFromName.ToleranceTableNumber, "ToleranceTableNumber");
        ensureInAttributeMetadata(TagFromName.ToleranceTableSequence, "ToleranceTableSequence");
        ensureInAttributeMetadata(TagFromName.TomoAngle, "TomoAngle");
        ensureInAttributeMetadata(TagFromName.TomoClass, "TomoClass");
        ensureInAttributeMetadata(TagFromName.TomoLayerHeight, "TomoLayerHeight");
        ensureInAttributeMetadata(TagFromName.TomoTime, "TomoTime");
        ensureInAttributeMetadata(TagFromName.TomoType, "TomoType");
        ensureInAttributeMetadata(TagFromName.TopicAuthor, "TopicAuthor");
        ensureInAttributeMetadata(TagFromName.TopicKeyWords, "TopicKeyWords");
        ensureInAttributeMetadata(TagFromName.TopicSubject, "TopicSubject");
        ensureInAttributeMetadata(TagFromName.TopicTitle, "TopicTitle");
        ensureInAttributeMetadata(TagFromName.TotalBlockTrayFactor, "TotalBlockTrayFactor");
        ensureInAttributeMetadata(TagFromName.TotalBlockTrayWaterEquivalentThickness,
                "TotalBlockTrayWaterEquivalentThickness");
        ensureInAttributeMetadata(TagFromName.TotalCollimationWidth, "TotalCollimationWidth");
        ensureInAttributeMetadata(TagFromName.TotalCompensatorTrayFactor, "TotalCompensatorTrayFactor");
        ensureInAttributeMetadata(TagFromName.TotalCompensatorTrayWaterEquivalentThickness,
                "TotalCompensatorTrayWaterEquivalentThickness");
        ensureInAttributeMetadata(TagFromName.TotalGain, "TotalGain");
        ensureInAttributeMetadata(TagFromName.TotalNumberOfExposures, "TotalNumberOfExposures");
        ensureInAttributeMetadata(TagFromName.TotalNumberOfPiecesOfMediaCreated, "TotalNumberOfPiecesOfMediaCreated");
        ensureInAttributeMetadata(TagFromName.TotalReferenceAirKerma, "TotalReferenceAirKerma");
        ensureInAttributeMetadata(TagFromName.TotalTime, "TotalTime");
        ensureInAttributeMetadata(TagFromName.TotalTimeOfFluoroscopy, "TotalTimeOfFluoroscopy");
        ensureInAttributeMetadata(TagFromName.TotalWedgeTrayWaterEquivalentThickness,
                "TotalWedgeTrayWaterEquivalentThickness");
        ensureInAttributeMetadata(TagFromName.TransactionUID, "TransactionUID");
        ensureInAttributeMetadata(TagFromName.TransducerData, "TransducerData");
        ensureInAttributeMetadata(TagFromName.TransducerFrequency, "TransducerFrequency");
        ensureInAttributeMetadata(TagFromName.TransducerOrientation, "TransducerOrientation");
        ensureInAttributeMetadata(TagFromName.TransducerOrientationModifierSequence,
                "TransducerOrientationModifierSequence");
        ensureInAttributeMetadata(TagFromName.TransducerOrientationSequence, "TransducerOrientationSequence");
        ensureInAttributeMetadata(TagFromName.TransducerPosition, "TransducerPosition");
        ensureInAttributeMetadata(TagFromName.TransducerPositionModifierSequence, "TransducerPositionModifierSequence");
        ensureInAttributeMetadata(TagFromName.TransducerPositionSequence, "TransducerPositionSequence");
        ensureInAttributeMetadata(TagFromName.TransducerType, "TransducerType");
        ensureInAttributeMetadata(TagFromName.TransferSyntaxUID, "TransferSyntaxUID");
        ensureInAttributeMetadata(TagFromName.TransferTubeLength, "TransferTubeLength");
        ensureInAttributeMetadata(TagFromName.TransferTubeNumber, "TransferTubeNumber");
        ensureInAttributeMetadata(TagFromName.TransformLabel, "TransformLabel");
        ensureInAttributeMetadata(TagFromName.TransformVersionNumber, "TransformVersionNumber");
        ensureInAttributeMetadata(TagFromName.TransmitCoilManufacturerName, "TransmitCoilManufacturerName");
        ensureInAttributeMetadata(TagFromName.TransmitCoilName, "TransmitCoilName");
        ensureInAttributeMetadata(TagFromName.TransmitCoilType, "TransmitCoilType");
        ensureInAttributeMetadata(TagFromName.TransmitterFrequency, "TransmitterFrequency");
        ensureInAttributeMetadata(TagFromName.TransverseMash, "TransverseMash");
        ensureInAttributeMetadata(TagFromName.TreatmentControlPointDate, "TreatmentControlPointDate");
        ensureInAttributeMetadata(TagFromName.TreatmentControlPointTime, "TreatmentControlPointTime");
        ensureInAttributeMetadata(TagFromName.TreatmentDate, "TreatmentDate");
        ensureInAttributeMetadata(TagFromName.TreatmentDeliveryType, "TreatmentDeliveryType");
        ensureInAttributeMetadata(TagFromName.TreatmentMachineName, "TreatmentMachineName");
        ensureInAttributeMetadata(TagFromName.TreatmentMachineSequence, "TreatmentMachineSequence");
        ensureInAttributeMetadata(TagFromName.TreatmentProtocols, "TreatmentProtocols");
        ensureInAttributeMetadata(TagFromName.TreatmentSessionApplicationSetupSequence,
                "TreatmentSessionApplicationSetupSequence");
        ensureInAttributeMetadata(TagFromName.TreatmentSessionBeamSequence, "TreatmentSessionBeamSequence");
        ensureInAttributeMetadata(TagFromName.TreatmentSessionIonBeamSequence, "TreatmentSessionIonBeamSequence");
        ensureInAttributeMetadata(TagFromName.TreatmentSites, "TreatmentSites");
        ensureInAttributeMetadata(TagFromName.TreatmentStatusComment, "TreatmentStatusComment");
        ensureInAttributeMetadata(TagFromName.TreatmentSummaryCalculatedDoseReferenceSequence,
                "TreatmentSummaryCalculatedDoseReferenceSequence");
        ensureInAttributeMetadata(TagFromName.TreatmentSummaryMeasuredDoseReferenceSequence,
                "TreatmentSummaryMeasuredDoseReferenceSequence");
        ensureInAttributeMetadata(TagFromName.TreatmentTerminationCode, "TreatmentTerminationCode");
        ensureInAttributeMetadata(TagFromName.TreatmentTerminationStatus, "TreatmentTerminationStatus");
        ensureInAttributeMetadata(TagFromName.TreatmentTime, "TreatmentTime");
        ensureInAttributeMetadata(TagFromName.TreatmentVerificationStatus, "TreatmentVerificationStatus");
        ensureInAttributeMetadata(TagFromName.TriggerSamplePosition, "TriggerSamplePosition");
        ensureInAttributeMetadata(TagFromName.TriggerSourceOrType, "TriggerSourceOrType");
        ensureInAttributeMetadata(TagFromName.TriggerTime, "TriggerTime");
        ensureInAttributeMetadata(TagFromName.TriggerTimeOffset, "TriggerTimeOffset");
        ensureInAttributeMetadata(TagFromName.TriggerVector, "TriggerVector");
        ensureInAttributeMetadata(TagFromName.TriggerWindow, "TriggerWindow");
        ensureInAttributeMetadata(TagFromName.Trim, "Trim");
        ensureInAttributeMetadata(TagFromName.TubeAngle, "TubeAngle");
        ensureInAttributeMetadata(TagFromName.TypeOfData, "TypeOfData");
        ensureInAttributeMetadata(TagFromName.TypeOfDetectorMotion, "TypeOfDetectorMotion");
        ensureInAttributeMetadata(TagFromName.TypeOfFilters, "TypeOfFilters");
        ensureInAttributeMetadata(TagFromName.TypeOfPatientID, "TypeOfPatientID");
        ensureInAttributeMetadata(TagFromName.UID, "UID");
        ensureInAttributeMetadata(TagFromName.UltrasoundColorDataPresent, "UltrasoundColorDataPresent");
        ensureInAttributeMetadata(TagFromName.UnformattedTextValue, "UnformattedTextValue");
        // ensureInAttributeMetadata(TagFromName.UniformResourceLocatorTrial,
        // "UniformResourceLocatorTrial");
        ensureInAttributeMetadata(TagFromName.Units, "Units");
        // ensureInAttributeMetadata(TagFromName.UniversalEntityID,
        // "UniversalEntityID");
        // ensureInAttributeMetadata(TagFromName.UniversalEntityIDType,
        // "UniversalEntityIDType");
        ensureInAttributeMetadata(TagFromName.UpperLowerPixelValues, "UpperLowerPixelValues");
        // ensureInAttributeMetadata(TagFromName.UrgencyOrPriorityAlertsTrial,
        // "UrgencyOrPriorityAlertsTrial");
        ensureInAttributeMetadata(TagFromName.UsedFiducialsSequence, "UsedFiducialsSequence");
        ensureInAttributeMetadata(TagFromName.ValueType, "ValueType");
        ensureInAttributeMetadata(TagFromName.VariableCoefficientsSDDN, "VariableCoefficientsSDDN");
        ensureInAttributeMetadata(TagFromName.VariableCoefficientsSDHN, "VariableCoefficientsSDHN");
        ensureInAttributeMetadata(TagFromName.VariableCoefficientsSDVN, "VariableCoefficientsSDVN");
        ensureInAttributeMetadata(TagFromName.VariableFlipAngleFlag, "VariableFlipAngleFlag");
        ensureInAttributeMetadata(TagFromName.VariableNextDataGroup, "VariableNextDataGroup");
        ensureInAttributeMetadata(TagFromName.VariablePixelData, "VariablePixelData");
        ensureInAttributeMetadata(TagFromName.VectorGridData, "VectorGridData");
        ensureInAttributeMetadata(TagFromName.VelocityEncodingDirection, "VelocityEncodingDirection");
        ensureInAttributeMetadata(TagFromName.VelocityEncodingMaximumValue, "VelocityEncodingMaximumValue");
        ensureInAttributeMetadata(TagFromName.VelocityEncodingMinimumValue, "VelocityEncodingMinimumValue");
        // ensureInAttributeMetadata(TagFromName.VerbalSourceIdentifierCodeSequenceTrial,
        // "VerbalSourceIdentifierCodeSequenceTrial");
        // ensureInAttributeMetadata(TagFromName.VerbalSourceTrial,
        // "VerbalSourceTrial");
        ensureInAttributeMetadata(TagFromName.VerificationDateTime, "VerificationDateTime");
        ensureInAttributeMetadata(TagFromName.VerificationFlag, "VerificationFlag");
        ensureInAttributeMetadata(TagFromName.VerifyingObserverIdentificationCodeSequence,
                "VerifyingObserverIdentificationCodeSequence");
        ensureInAttributeMetadata(TagFromName.VerifyingObserverName, "VerifyingObserverName");
        ensureInAttributeMetadata(TagFromName.VerifyingObserverSequence, "VerifyingObserverSequence");
        ensureInAttributeMetadata(TagFromName.VerifyingOrganization, "VerifyingOrganization");
        ensureInAttributeMetadata(TagFromName.VerticesOfPolygonalCollimator, "VerticesOfPolygonalCollimator");
        ensureInAttributeMetadata(TagFromName.VerticesOfPolygonalExposureControlSensingRegion,
                "VerticesOfPolygonalExposureControlSensingRegion");
        ensureInAttributeMetadata(TagFromName.VerticesOfPolygonalShutter, "VerticesOfPolygonalShutter");
        ensureInAttributeMetadata(TagFromName.VideoImageFormatAcquired, "VideoImageFormatAcquired");
        ensureInAttributeMetadata(TagFromName.ViewCodeSequence, "ViewCodeSequence");
        ensureInAttributeMetadata(TagFromName.ViewModifierCodeSequence, "ViewModifierCodeSequence");
        ensureInAttributeMetadata(TagFromName.ViewName, "ViewName");
        ensureInAttributeMetadata(TagFromName.ViewNumber, "ViewNumber");
        ensureInAttributeMetadata(TagFromName.ViewPosition, "ViewPosition");
        ensureInAttributeMetadata(TagFromName.VirtualSourceAxisDistances, "VirtualSourceAxisDistances");
        ensureInAttributeMetadata(TagFromName.VisitComments, "VisitComments");
        ensureInAttributeMetadata(TagFromName.VisitStatusID, "VisitStatusID");
        ensureInAttributeMetadata(TagFromName.VitalStainCodeSequenceTrial, "VitalStainCodeSequenceTrial");
        ensureInAttributeMetadata(TagFromName.VOILUTFunction, "VOILUTFunction");
        ensureInAttributeMetadata(TagFromName.VOILUTSequence, "VOILUTSequence");
        ensureInAttributeMetadata(TagFromName.VOIType, "VOIType");
        ensureInAttributeMetadata(TagFromName.VolumeBasedCalculationTechnique, "VolumeBasedCalculationTechnique");
        ensureInAttributeMetadata(TagFromName.VolumeLocalizationSequence, "VolumeLocalizationSequence");
        ensureInAttributeMetadata(TagFromName.VolumeLocalizationTechnique, "VolumeLocalizationTechnique");
        ensureInAttributeMetadata(TagFromName.VolumetricProperties, "VolumetricProperties");
        ensureInAttributeMetadata(TagFromName.WaterReferencedPhaseCorrection, "WaterReferencedPhaseCorrection");
        // ensureInAttributeMetadata(TagFromName.WaveformAnnotationSequence,
        // "WaveformAnnotationSequence");
        ensureInAttributeMetadata(TagFromName.WaveformBitsAllocated, "WaveformBitsAllocated");
        ensureInAttributeMetadata(TagFromName.WaveformBitsStored, "WaveformBitsStored");
        ensureInAttributeMetadata(TagFromName.WaveformChannelNumber, "WaveformChannelNumber");
        ensureInAttributeMetadata(TagFromName.WaveformData, "WaveformData");
        ensureInAttributeMetadata(TagFromName.WaveformDataDisplayScale, "WaveformDataDisplayScale");
        ensureInAttributeMetadata(TagFromName.WaveformDisplayBackgroundCIELabValue,
                "WaveformDisplayBackgroundCIELabValue");
        ensureInAttributeMetadata(TagFromName.WaveformOriginality, "WaveformOriginality");
        ensureInAttributeMetadata(TagFromName.WaveformPaddingValue, "WaveformPaddingValue");
        ensureInAttributeMetadata(TagFromName.WaveformPresentationGroupSequence, "WaveformPresentationGroupSequence");
        ensureInAttributeMetadata(TagFromName.WaveformSampleInterpretation, "WaveformSampleInterpretation");
        ensureInAttributeMetadata(TagFromName.WaveformSequence, "WaveformSequence");
        ensureInAttributeMetadata(TagFromName.WedgeAngle, "WedgeAngle");
        ensureInAttributeMetadata(TagFromName.WedgeFactor, "WedgeFactor");
        ensureInAttributeMetadata(TagFromName.WedgeID, "WedgeID");
        ensureInAttributeMetadata(TagFromName.WedgeNumber, "WedgeNumber");
        ensureInAttributeMetadata(TagFromName.WedgeOrientation, "WedgeOrientation");
        ensureInAttributeMetadata(TagFromName.WedgePosition, "WedgePosition");
        ensureInAttributeMetadata(TagFromName.WedgePositionSequence, "WedgePositionSequence");
        ensureInAttributeMetadata(TagFromName.WedgeSequence, "WedgeSequence");
        ensureInAttributeMetadata(TagFromName.WedgeThinEdgePosition, "WedgeThinEdgePosition");
        ensureInAttributeMetadata(TagFromName.WedgeType, "WedgeType");
        ensureInAttributeMetadata(TagFromName.WholeBodyTechnique, "WholeBodyTechnique");
        ensureInAttributeMetadata(TagFromName.WindowCenter, "WindowCenter");
        ensureInAttributeMetadata(TagFromName.WindowCenterWidthExplanation, "WindowCenterWidthExplanation");
        ensureInAttributeMetadata(TagFromName.WindowWidth, "WindowWidth");
        ensureInAttributeMetadata(TagFromName.XAXRFFrameCharacteristicsSequence, "XAXRFFrameCharacteristicsSequence");
        ensureInAttributeMetadata(TagFromName.XFocusCenter, "XFocusCenter");
        ensureInAttributeMetadata(TagFromName.XOffsetInSlideCoordinateSystem, "XOffsetInSlideCoordinateSystem");
        ensureInAttributeMetadata(TagFromName.XRay3DAcquisitionSequence, "XRay3DAcquisitionSequence");
        ensureInAttributeMetadata(TagFromName.XRay3DFrameTypeSequence, "XRay3DFrameTypeSequence");
        ensureInAttributeMetadata(TagFromName.XRay3DReconstructionSequence, "XRay3DReconstructionSequence");
        ensureInAttributeMetadata(TagFromName.XRayGeometrySequence, "XRayGeometrySequence");
        ensureInAttributeMetadata(TagFromName.XRayImageReceptorAngle, "XRayImageReceptorAngle");
        ensureInAttributeMetadata(TagFromName.XRayImageReceptorTranslation, "XRayImageReceptorTranslation");
        ensureInAttributeMetadata(TagFromName.XRayOutput, "XRayOutput");
        ensureInAttributeMetadata(TagFromName.XRayReceptorType, "XRayReceptorType");
        ensureInAttributeMetadata(TagFromName.XRayTubeCurrent, "XRayTubeCurrent");
        ensureInAttributeMetadata(TagFromName.XRayTubeCurrentInmA, "XRayTubeCurrentInmA");
        ensureInAttributeMetadata(TagFromName.XRayTubeCurrentInuA, "XRayTubeCurrentInuA");
        ensureInAttributeMetadata(TagFromName.YFocusCenter, "YFocusCenter");
        ensureInAttributeMetadata(TagFromName.YOffsetInSlideCoordinateSystem, "YOffsetInSlideCoordinateSystem");
        ensureInAttributeMetadata(TagFromName.ZOffsetInSlideCoordinateSystem, "ZOffsetInSlideCoordinateSystem");
        ensureInAttributeMetadata(TagFromName.ZonalMap, "ZonalMap");
        ensureInAttributeMetadata(TagFromName.ZonalMapFormat, "ZonalMapFormat");
        ensureInAttributeMetadata(TagFromName.ZonalMapLocation, "ZonalMapLocation");
        ensureInAttributeMetadata(TagFromName.ZonalMapNumberFormat, "ZonalMapNumberFormat");
        ensureInAttributeMetadata(TagFromName.ZoomCenter, "ZoomCenter");
        ensureInAttributeMetadata(TagFromName.ZoomFactor, "ZoomFactor ");
    }

    private static void ensureInAttributeMetadata(AttributeTag tag, String name) {
//        attributeMetadataMap.put(tag, null);
//        tagNameMap.put(tag, name);
    }
//
//    private AttributeMetadataDAO dao = new AttributeMetadataDAO();

    /**
     * Constructor
     */
    DICOMTagToAuthAttributeMetadataMapper() {
        super();
    }

//    /**
//     * Given an attribute tag return an object that contains metadata for an
//     * equivalent data attribute in the authorization database. If no equivalent
//     * metadata exists in the authorization database, it is created.
//     * 
//     * @param tag
//     *            the tag to find equivalent metadata for.
//     * @return the equivalent metadata or null if the tag is not supported.
//     */
//    AttributeMetadata attributeTagToAttributeMetadata(AttributeTag tag) {
//        AttributeMetadata metadata = attributeMetadataMap.get(tag);
//        if (metadata == null) {
//            // Metadata not yet in dictionary.
//            String name = tagNameMap.get(tag);
//            if (name != null) {
//                // The tag is recognized as having a name in tagMapName.
//                List<AttributeMetadata> metaList = dao.findByAttrName(name);
//                if (metaList.size() > 0) {
//                    // We fetched the metadata from the database
//                    metadata = metaList.get(0);
//                    // cache the metadata in the map.
//                    attributeMetadataMap.put(tag, metadata);
//                } else {
//                    // metadata is not in the database.
//                    metadata = new AttributeMetadata(name);
//                    dao.beginTransaction();
//                    dao.save(metadata);
//                    dao.commit();
//                }
//            }
//        }
//        return metadata;
//    }
}
