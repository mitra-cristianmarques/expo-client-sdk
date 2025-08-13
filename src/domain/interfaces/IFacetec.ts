import { FacetecParams, FacetecResponse } from '../models'

export interface IFacetec {
  Facetec(params: FacetecParams): Promise<FacetecResponse>
}
