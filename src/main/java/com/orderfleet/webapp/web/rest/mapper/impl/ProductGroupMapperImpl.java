package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.ProductGroupMapper;
@Component
public class ProductGroupMapperImpl extends ProductGroupMapper{

	  @Override
	    public ProductGroupDTO productGroupToProductGroupDTO(ProductGroup productGroup) {
	        if ( productGroup == null ) {
	            return null;
	        }

	        ProductGroupDTO productGroupDTO = new ProductGroupDTO();

	        productGroupDTO.setPid( productGroup.getPid() );
	        productGroupDTO.setName( productGroupName(productGroup) );
	        productGroupDTO.setAlias( productGroup.getAlias() );
	        productGroupDTO.setDescription( productGroup.getDescription() );
	        productGroupDTO.setImageContentType( productGroup.getImageContentType() );
	        productGroupDTO.setLastModifiedDate( productGroup.getLastModifiedDate() );
	        productGroupDTO.setProductGroupId( productGroup.getProductGroupId() );
	        productGroupDTO.setActivated( productGroup.getActivated() );
	        productGroupDTO.setThirdpartyUpdate( productGroup.getThirdpartyUpdate() );

	        return productGroupDTO;
	    }

	    @Override
	    public List<ProductGroupDTO> productGroupsToProductGroupDTOs(List<ProductGroup> productGroups) {
	        if ( productGroups == null ) {
	            return null;
	        }

	        List<ProductGroupDTO> list = new ArrayList<ProductGroupDTO>();
	        for ( ProductGroup productGroup : productGroups ) {
	            list.add( productGroupToProductGroupDTO( productGroup ) );
	        }

	        return list;
	    }

	    @Override
	    public ProductGroup productGroupDTOToProductGroup(ProductGroupDTO productGroupDTO) {
	        if ( productGroupDTO == null ) {
	            return null;
	        }

	        ProductGroup productGroup = new ProductGroup();

	        productGroup.setPid( productGroupDTO.getPid() );
	        productGroup.setName( productGroupDTO.getName() );
	        productGroup.setAlias( productGroupDTO.getAlias() );
	        productGroup.setDescription( productGroupDTO.getDescription() );
	        productGroup.setProductGroupId( productGroupDTO.getProductGroupId() );
	        if ( productGroupDTO.getImage() != null ) {
	            byte[] image = productGroupDTO.getImage();
	            productGroup.setImage( Arrays.copyOf( image, image.length ) );
	        }
	        productGroup.setImageContentType( productGroupDTO.getImageContentType() );
	        productGroup.setActivated( productGroupDTO.getActivated() );
	        productGroup.setThirdpartyUpdate( productGroupDTO.getThirdpartyUpdate() );

	        return productGroup;
	    }

	    @Override
	    public List<ProductGroup> productGroupDTOsToProductGroups(List<ProductGroupDTO> productGroupDTOs) {
	        if ( productGroupDTOs == null ) {
	            return null;
	        }

	        List<ProductGroup> list = new ArrayList<ProductGroup>();
	        for ( ProductGroupDTO productGroupDTO : productGroupDTOs ) {
	            list.add( productGroupDTOToProductGroup( productGroupDTO ) );
	        }

	        return list;
	    }
	    private String productGroupName(ProductGroup productGroup) {
	        if(productGroup.getDescription()!=null && getCompanyCofig() && !productGroup.getDescription().equals("common")) {
	        return productGroup.getDescription();
	        }
	       
	    return productGroup.getName();
	    }
}
